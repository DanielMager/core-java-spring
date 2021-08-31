package eu.arrowhead.core.orchestrator.protocols.mqtt;

//import eu.arrowhead.core.orchestrator.protocols.mqtt.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.SslUtil;
import eu.arrowhead.common.dto.shared.CloudRequestDTO;
import eu.arrowhead.common.dto.shared.OrchestrationFlags.Flag;
import eu.arrowhead.common.dto.shared.OrchestrationFormRequestDTO;
import eu.arrowhead.common.dto.shared.PreferredProviderDataDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;
import eu.arrowhead.common.dto.shared.ServiceRegistryRequestDTO;
import eu.arrowhead.common.dto.shared.MqttRequestDTO;
import eu.arrowhead.common.dto.shared.MqttResponseDTO;
import eu.arrowhead.core.orchestrator.service.OrchestratorService;

import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

@Component
@EnableScheduling
public class MqttOrchestrator implements MqttCallback {

  // =================================================================================================
  // members
  private final Logger logger = LogManager.getLogger(MqttOrchestrator.class);

  @Value(CoreCommonConstants.$ORCHESTRATOR_IS_GATEKEEPER_PRESENT_WD)
  private boolean gatekeeperIsPresent;

  @Autowired
  private OrchestratorService orchestratorService;

  @Value(CoreCommonConstants.$CORE_SYSTEM_NAME)
  private String mqttSystemName;

  @Value(CoreCommonConstants.$MQTT_BROKER_ENABLED)
  private boolean mqttBrokerEnabled;

  @Value(CoreCommonConstants.$MQTT_BROKER_ADDRESS)
  private String mqttBrokerAddress;

  @Value(CoreCommonConstants.$MQTT_BROKER_PORT)
  private int mqttBrokerPort;

  @Value(CoreCommonConstants.$MQTT_BROKER_USERNAME)
  private String mqttBrokerUsername;

  @Value(CoreCommonConstants.$MQTT_BROKER_PASSWORD)
  private String mqttBrokerPassword;

  @Value(CoreCommonConstants.$MQTT_BROKER_CAFILE)
  private String mqttBrokerCAFile;

  @Value(CoreCommonConstants.$MQTT_BROKER_CERTFILE)
  private String mqttBrokerCertFile;

  @Value(CoreCommonConstants.$MQTT_BROKER_KEYFILE)
  private String mqttBrokerKeyFile;

  private final String URL_PATH_ORCHESTRATOR = "orchestrator";
  private final String URL_PATH_ID = "id";

  private final String ECHO_TOPIC = "ah/" + URL_PATH_ORCHESTRATOR + "/echo";
  private final String ORCHESTRATION_TOPIC = "ah/" + URL_PATH_ORCHESTRATOR + "/orchestration";
  private final String ORCHESTRATION_BY_ID_TOPIC = "ah/" + URL_PATH_ORCHESTRATOR + "/orchestration/" + URL_PATH_ID;

  private final String ORCH_RESPONSE_TOPIC = "ah/" + URL_PATH_ORCHESTRATOR + "/replies";

  private boolean registeredWithServiceRegistry = false;

  final String GET_METHOD = "get";
  final String POST_METHOD = "post";
  final String DELETE_METHOD = "delete";

  // =================================================================================================
  // methods
  // -------------------------------------------------------------------------------------------------
  @PostConstruct
  public void init() {
    
    if (mqttBrokerEnabled) {
      logger.info("Starting MQTT protocol");

      if(Utilities.isEmpty(mqttBrokerUsername) || Utilities.isEmpty(mqttBrokerPassword)) {
        logger.info("Missing MQTT broker username or password!");
        //System.exit(-1);
      }

      if(Utilities.isEmpty(mqttBrokerCAFile) || Utilities.isEmpty(mqttBrokerCertFile) || Utilities.isEmpty(mqttBrokerKeyFile)) {
        logger.info("Missing MQTT broker certificate/key files!");
        //System.exit(-1);
      }
      
    }
  }

  MqttClient client = null;
  MemoryPersistence persistence = null;

  private void connectBroker() {
    MemoryPersistence persistence = new MemoryPersistence();

    try {
      MqttConnectOptions connOpts = new MqttConnectOptions();
      
      if(!Utilities.isEmpty(mqttBrokerUsername) && !Utilities.isEmpty(mqttBrokerPassword)) {
        connOpts.setUserName(mqttBrokerUsername);
        connOpts.setPassword(mqttBrokerPassword.toCharArray());
      }

      connOpts.setCleanSession(true);
      connOpts.setConnectionTimeout(60);
      connOpts.setKeepAliveInterval(60);
      connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

      if(!Utilities.isEmpty(mqttBrokerCAFile) && !Utilities.isEmpty(mqttBrokerCertFile) && !Utilities.isEmpty(mqttBrokerKeyFile)) {
        SSLSocketFactory socketFactory = null;
        try {
          socketFactory = SslUtil.getSslSocketFactory(mqttBrokerCAFile, mqttBrokerCertFile, mqttBrokerKeyFile, "");
        } catch (Exception e) {
          logger.info("Could not load certificate file(s): " + e.toString());
        }
        connOpts.setSocketFactory(socketFactory);
      }

      client.setCallback(this);
      client.connect(connOpts);

      String topics[] = { ECHO_TOPIC, ORCHESTRATION_TOPIC, ORCHESTRATION_BY_ID_TOPIC, ORCH_RESPONSE_TOPIC };
      client.subscribe(topics);
    } catch (MqttException me) {
      logger.info("Could not connect to MQTT broker!\n\t" + me.toString());
    }

  }

  // =================================================================================================
  // assistant methods
  // -------------------------------------------------------------------------------------------------
  private void checkOrchestratorFormRequestDTO(final OrchestrationFormRequestDTO request, final String origin)
      throws Exception {
    if (request == null) {
      throw new Exception("Request null");
    }

    request.validateCrossParameterConstraints();

    // Requester system
    checkSystemRequestDTO(request.getRequesterSystem(), origin);

    // Requester cloud
    if (request.getRequesterCloud() != null) {
      checkCloudRequestDTO(request.getRequesterCloud(), origin);
    }

    // Requested service
    if (request.getRequestedService() != null
        && Utilities.isEmpty(request.getRequestedService().getServiceDefinitionRequirement())) {
      throw new Exception("Requested service definition requirement");
    }

    // Preferred Providers
    if (request.getPreferredProviders() != null) {
      for (final PreferredProviderDataDTO provider : request.getPreferredProviders()) {
        checkSystemRequestDTO(provider.getProviderSystem(), origin);
        if (provider.getProviderCloud() != null) {
          checkCloudRequestDTO(provider.getProviderCloud(), origin);
        }
      }
    }
  }

  // -------------------------------------------------------------------------------------------------
  private void checkSystemRequestDTO(final SystemRequestDTO system, final String origin) throws Exception {
    logger.debug("checkSystemRequestDTO started...");

    if (system == null) {
      throw new Exception("System null");
    }

    if (Utilities.isEmpty(system.getSystemName())) {
      throw new Exception("System name null");
    }

    if (Utilities.isEmpty(system.getAddress())) {
      throw new Exception("System address null");
    }

    if (system.getPort() == null) {
      throw new Exception("System port null");
    }

    final int validatedPort = system.getPort().intValue();
    if (validatedPort < CommonConstants.SYSTEM_PORT_RANGE_MIN
        || validatedPort > CommonConstants.SYSTEM_PORT_RANGE_MAX) {
      throw new Exception("System port must be between " + CommonConstants.SYSTEM_PORT_RANGE_MIN + " and "
          + CommonConstants.SYSTEM_PORT_RANGE_MAX + ".");
    }
  }

  // -------------------------------------------------------------------------------------------------
  private void checkCloudRequestDTO(final CloudRequestDTO cloud, final String origin) throws Exception {
    logger.debug("checkCloudRequestDTO started...");

    if (cloud == null) {
      throw new Exception("Cloud null");
    }

    if (Utilities.isEmpty(cloud.getOperator())) {
      throw new Exception("Cloud operator null");
    }

    if (Utilities.isEmpty(cloud.getName())) {
      throw new Exception("Cloud name null");
    }
  }

  @Scheduled(fixedDelay=1000*60, initialDelay = 1000*5)
  public void manageBroker() {

    try {
      if (client == null) {
        persistence = new MemoryPersistence();
        registeredWithServiceRegistry = false;
        if(!Utilities.isEmpty(mqttBrokerCAFile) && !Utilities.isEmpty(mqttBrokerCertFile) && !Utilities.isEmpty(mqttBrokerKeyFile)) {
          client = new MqttClient(CommonConstants.PROTOCOL_SSL + mqttBrokerAddress + ":" + mqttBrokerPort, mqttSystemName, persistence);
        } else {
          client = new MqttClient(CommonConstants.PROTOCOL_TCP + mqttBrokerAddress + ":" + mqttBrokerPort, mqttSystemName, persistence);
        }
      }

      if (!client.isConnected()) {
        registeredWithServiceRegistry = false;
        connectBroker();
      }
      
      if (client.isConnected() && !registeredWithServiceRegistry) {
        doRegister();
      }

    } catch (MqttException mex) {
      logger.info("MQTT error: " + mex.toString());
    }
  }

  void doRegister(){
    logger.info("Registering MQTT services with ServiceRegistry");

    ServiceRegistryRequestDTO srRegRequest = new ServiceRegistryRequestDTO();
    srRegRequest.setServiceUri(ORCHESTRATION_TOPIC);
    srRegRequest.setServiceDefinition("");
    if(!Utilities.isEmpty(mqttBrokerCAFile) && !Utilities.isEmpty(mqttBrokerCertFile) && !Utilities.isEmpty(mqttBrokerKeyFile)) {
      srRegRequest.setSecure("CERTIFICATE");
    } else {
      srRegRequest.setSecure("NOT_SECURE");
    }
    //srRegRequest.setEndOfValidity("");
    srRegRequest.setInterfaces(List.of("MQTT-INSECURE-JSON"));
    MqttRequestDTO request = new MqttRequestDTO(POST_METHOD, null, ORCH_RESPONSE_TOPIC, srRegRequest);
    final String respJson = Utilities.toJson(request); 
    System.out.println(respJson);
    final MqttMessage resp = new MqttMessage(Utilities.toJson(request).getBytes());

    registeredWithServiceRegistry = true; //XXX fixme
    
    try {
      client.publish("ah/serviceregister/register", resp);
    } catch (MqttException mex){
      logger.debug("Could not register service");
    }
  }

  @Override
  public void connectionLost(Throwable cause) {
    logger.info("Connection lost to MQTT broker");
    client = null;
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) {
    MqttRequestDTO request = null;
    MqttResponseDTO response = null;
    ObjectMapper mapper;

    try {
      mapper = new ObjectMapper();
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      request = mapper.readValue(message.toString(), MqttRequestDTO.class);
    } catch (Exception ae) {
      logger.info("Could not convert MQTT message to REST request!");
      return;
    }

    logger.info(request.toString());

    switch (topic) {
      case ECHO_TOPIC:
        logger.info("ah/orchestration/echo(): " + new String(message.getPayload(), StandardCharsets.UTF_8));
        if (!request.getMethod().equalsIgnoreCase("get")) {
          return;
        }
        try {
          response = new MqttResponseDTO("200", "text/plain", "Got it");
          MqttMessage resp = new MqttMessage(Utilities.toJson(response).getBytes());
          resp.setQos(2);
          client.publish(request.getReplyTo(), resp);
        } catch (MqttException mex) {
          logger.info("echo(): Couldn't reply " + mex.toString());
        }
        break;
      case ORCHESTRATION_TOPIC:
        logger.info("ah/orchestration(): " + new String(message.getPayload(), StandardCharsets.UTF_8));
        if (!request.getMethod().equalsIgnoreCase("post")) {
          return;
        }

        try {
          OrchestrationFormRequestDTO orchRequest = mapper.convertValue(request.getPayload(), OrchestrationFormRequestDTO.class);

          final String origin = CommonConstants.ORCHESTRATOR_URI + CommonConstants.OP_ORCH_PROCESS_URI;
          checkOrchestratorFormRequestDTO(orchRequest, origin);

          if (orchRequest.getOrchestrationFlags().getOrDefault(Flag.EXTERNAL_SERVICE_REQUEST, false)) {
            if (!gatekeeperIsPresent) {
              throw new Exception("External service request, Gatekeeper is not present.");
            }
            response = new MqttResponseDTO("200", "application/json", null);
            response.setPayload(orchestratorService.externalServiceRequest(orchRequest));
            MqttMessage resp = new MqttMessage(mapper.writeValueAsString(response).getBytes());
            resp.setQos(2);
            client.publish(request.getReplyTo(), resp);
          } else if (orchRequest.getOrchestrationFlags().getOrDefault(Flag.TRIGGER_INTER_CLOUD, false)) {
            if (!gatekeeperIsPresent) {
              throw new Exception("External service request, Gatekeeper is not present.");
            }
            response = new MqttResponseDTO("200", "application/json", null);
            response.setPayload(orchestratorService.triggerInterCloud(orchRequest));
            MqttMessage resp = new MqttMessage(mapper.writeValueAsString(response).getBytes());
            resp.setQos(2);
            client.publish(request.getReplyTo(), resp);
          } else if (!orchRequest.getOrchestrationFlags().getOrDefault(Flag.OVERRIDE_STORE, false)) {
            response = new MqttResponseDTO("200", "application/json", null);
            response.setPayload(orchestratorService.orchestrationFromStore(orchRequest));
            MqttMessage resp = new MqttMessage(mapper.writeValueAsString(response).getBytes());
            resp.setQos(2);
            client.publish(request.getReplyTo(), resp);
          } else {
            response = new MqttResponseDTO("200", "application/json", null);
            response.setPayload(orchestratorService.dynamicOrchestration(orchRequest));
            MqttMessage resp = new MqttMessage(mapper.writeValueAsString(response).getBytes());
            resp.setQos(2);
            client.publish(request.getReplyTo(), resp);
          }

        } catch (Exception ex) {
          try {
            response = new MqttResponseDTO("500", "text/plain", null);
            MqttMessage resp = new MqttMessage(mapper.writeValueAsString(response).getBytes());
            resp.setQos(2);
            client.publish(request.getReplyTo(), resp);
          } catch (Exception mex) {
          }
        }
        break;
      case ORCHESTRATION_BY_ID_TOPIC:
        logger.info("orchestration/id(): " + new String(message.getPayload(), StandardCharsets.UTF_8));
        if (!request.getMethod().toLowerCase().equals("post")) {
          return;
        }

        try {
          int id = Integer.parseInt(request.getQueryParameters().get("id"));

          if (id < 1) {
            throw new Exception("Id not valid");
          }

          response = new MqttResponseDTO("200", "application/json", null);
          response.setPayload(orchestratorService.storeOchestrationProcessResponse(id));
          MqttMessage resp = new MqttMessage(mapper.writeValueAsString(response).getBytes());
          resp.setQos(2);
          client.publish(request.getReplyTo(), resp);
        } catch (Exception e) {
          logger.info("illegal request: " + e.toString());
        }

        break;
      default:
        logger.info("Received message to unsupported topic");
    }

  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {

  }

}

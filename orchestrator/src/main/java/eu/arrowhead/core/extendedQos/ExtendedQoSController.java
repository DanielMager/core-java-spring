package eu.arrowhead.core.extendedQos;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.Defaults;
import eu.arrowhead.common.database.entity.QoSPolicy;
import eu.arrowhead.common.dto.shared.QoSEvaluatorListResponseDTO;
import eu.arrowhead.common.dto.shared.QoSPolicyCreateRequestDTO;
import eu.arrowhead.common.dto.shared.QoSPolicyListResponseDTO;
import eu.arrowhead.common.dto.shared.QoSPolicyResponseDTO;
import eu.arrowhead.common.dto.shared.QoSPolicyUpdateRequestDTO;
import eu.arrowhead.common.exception.BadPayloadException;
import eu.arrowhead.core.extendedQos.database.ExtendedQoSDBService;
import eu.arrowhead.core.extendedQos.evaluator.QoSEvaluatorLoader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST Controller providing endpoints used for working with the new QoS
 * concept.
 */
@Api(tags = { CoreCommonConstants.SWAGGER_TAG_ALL })
@CrossOrigin(maxAge = Defaults.CORS_MAX_AGE, allowCredentials = Defaults.CORS_ALLOW_CREDENTIALS, allowedHeaders = {
		HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION })
@RestController
@RequestMapping(CommonConstants.ORCHESTRATOR_URI)
public class ExtendedQoSController {

	private static final String GET_QOS_POLICIES_HTTP_200_MESSAGE = "All QoS policies returned";

	private static final String CREATE_QOS_POLICIES_HTTP_200_MESSAGE = "QoS policy with requested parameters created";
	private static final String CREATE_QOS_POLICIES_HTTP_400_MESSAGE = "Could not create QoS policy with requested parameters";

	private static final String UPDATE_QOS_POLICIES_HTTP_200_MESSAGE = "QoS policy with requested parameters updated";
	private static final String UPDATE_QOS_POLICIES_HTTP_400_MESSAGE = "Could not update QoS policy with requested parameters";

	private static final String DELETE_QOS_POLICIES_HTTP_200_MESSAGE = "QoS policy removed";
	private static final String DELETE_QOS_POLICIES_HTTP_400_MESSAGE = "Could not remove QoS policy";

	private static final String GET_GLOBAL_DEFAULT_QOS_POLICY_HTTP_200_MESSAGE = "Default QoS policy returned";
	private static final String SET_GLOBAL_DEFAULT_QOS_POLICY_HTTP_200_MESSAGE = "Global default QoS policy with requested parameters created";
	private static final String SET_GLOBAL_DEFAULT_QOS_POLICY_HTTP_400_MESSAGE = "Could not create global default QoS policy with requested parameters";

	private static final String GET_SYSTEM_DEFAULT_QOS_POLICY_HTTP_200_MESSAGE = "Default QoS policy for the system returned";
	private static final String SET_SYSTEM_DEFAULT_QOS_POLICY_HTTP_200_MESSAGE = "Service default QoS policy with requested parameters created";
	private static final String SET_SYSTEM_DEFAULT_QOS_POLICY_HTTP_400_MESSAGE = "Could not create global default QoS policy with requested parameters";

	private static final String GET_QOS_EVALUATORS_HTTP_200_MESSAGE = "All available QoS evaluatos returned";

	private static final String EXTENDED_QOS_GET_POLICIES = CommonConstants.EXTENDED_QOS_URI
			+ CommonConstants.OP_EXTENDED_QOS_POLICIES;
	private static final String EXTENDED_QOS_CREATE_POLICY = CoreCommonConstants.MGMT_URI
			+ CommonConstants.EXTENDED_QOS_URI + CommonConstants.OP_EXTENDED_QOS_CREATE_POLICY;
	private static final String EXTENDED_QOS_UPDATE_POLICY = CoreCommonConstants.MGMT_URI
			+ CommonConstants.EXTENDED_QOS_URI + CommonConstants.OP_EXTENDED_QOS_UPDATE_POLICY;
	private static final String EXTENDED_QOS_DELETE_POLICY = CoreCommonConstants.MGMT_URI
			+ CommonConstants.EXTENDED_QOS_URI + CommonConstants.OP_EXTENDED_QOS_DELETE_POLICY;

	private static final String EXTENDED_QOS_GET_GLOBAL_DEFAULT_POLICY = CommonConstants.EXTENDED_QOS_URI
			+ CommonConstants.OP_EXTENDED_QOS_POLICIES + CommonConstants.OP_EXTENDED_QOS_GET_GLOBAL_DEFAULT_POLICY;
	private static final String EXTENDED_QOS_SET_GLOBAL_DEFAULT_POLICY = CoreCommonConstants.MGMT_URI
			+ CommonConstants.EXTENDED_QOS_URI + CommonConstants.OP_EXTENDED_QOS_POLICIES
			+ CommonConstants.OP_EXTENDED_QOS_SET_GLOBAL_DEFAULT_POLICY;

	private static final String EXTENDED_QOS_GET_SYSTEM_DEFAULT_POLICY = CommonConstants.EXTENDED_QOS_URI
			+ CommonConstants.OP_EXTENDED_QOS_POLICIES + CommonConstants.OP_EXTENDED_QOS_GET_SYSTEM_DEFAULT_POLICY;
	private static final String EXTENDED_QOS_SET_SYSTEM_DEFAULT_POLICY = CoreCommonConstants.MGMT_URI
			+ CommonConstants.EXTENDED_QOS_URI + CommonConstants.OP_EXTENDED_QOS_POLICIES
			+ CommonConstants.OP_EXTENDED_QOS_SET_SYSTEM_DEFAULT_POLICY;

	private static final String EXTENDED_QOS_GET_EVALUATORS = CoreCommonConstants.MGMT_URI
			+ CommonConstants.EXTENDED_QOS_URI + CommonConstants.OP_EXTENDED_QOS_EVALUATORS;

	private static final String PATH_VARIABLE_ID = "id";
	private static final String PATH_VARIABLE_SYSTEM_ID = "systemId";
	private static final String PATH_VARIABLE_POLICY_ID = "policyId";

	private static final String ID_NOT_VALID_ERROR_MESSAGE = "id must be greater 0!";

	@Autowired
	private ExtendedQoSDBService extendedQoSDBService;

	@Autowired
	QoSEvaluatorLoader evaluatorLoader;

	private final Logger logger = LogManager.getLogger(ExtendedQoSController.class);

	/**
	 * Returns all QoS policies that are registered in the system and saved in the
	 * database.
	 * 
	 * @return {@link QoSPolicyListResponseDTO}
	 */
	@ApiOperation(value = "Return all available QoS policies", response = QoSPolicyListResponseDTO.class, tags = {
			CoreCommonConstants.SWAGGER_TAG_CLIENT })
	@ApiResponses(value = { @ApiResponse(code = HttpStatus.SC_OK, message = GET_QOS_POLICIES_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@GetMapping(path = EXTENDED_QOS_GET_POLICIES, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public QoSPolicyListResponseDTO getAllQoSPolicies() {
		logger.debug("getAllQoSPolicies started ...");
		return new QoSPolicyListResponseDTO(extendedQoSDBService.getAllPolicies());
	}

	/**
	 * Creates a new QoS policy from the data that is provided with the POST
	 * request.
	 * 
	 * @param policy {@link QoSPolicyCreateRequestDTO}
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@ApiOperation(value = "Create requested QoS policy entry", response = QoSPolicy.class, tags = {
			CoreCommonConstants.SWAGGER_TAG_MGMT })
	@ApiResponses(value = { @ApiResponse(code = HttpStatus.SC_OK, message = CREATE_QOS_POLICIES_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = CREATE_QOS_POLICIES_HTTP_400_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@PostMapping(path = EXTENDED_QOS_CREATE_POLICY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public QoSPolicyResponseDTO addQoSPolicy(@RequestBody final QoSPolicyCreateRequestDTO policy) {
		logger.debug("addQoSPolicy started ...");

		if (policy == null) {
			throw new BadPayloadException("policy is null");
		}
		if (policy.getName() == null || policy.getName().isBlank()) {
			throw new BadPayloadException("policy name is null");
		}
		if (policy.getDescription() == null || policy.getDescription().isBlank()) {
			throw new BadPayloadException("policy description is null");
		}
		if (policy.getEvaluator() == null || policy.getEvaluator().isBlank()) {
			throw new BadPayloadException("policy evaluator is null");
		}
		if (policy.getParameters() == null) {
			throw new BadPayloadException("policy parameters is null");
		}

		QoSPolicyResponseDTO created = extendedQoSDBService.createQoSPolicy(policy);
		logger.debug("Created new policy with id '{}'", created.getId());

		return created;
	}

	/**
	 * Updates the policy specified in POST parameters with new data.
	 * 
	 * @param policy {@link QoSPolicyUpdateRequestDTO}
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@ApiOperation(value = "Update requested QoS policy entry.", response = QoSPolicy.class, tags = {
			CoreCommonConstants.SWAGGER_TAG_MGMT })
	@ApiResponses(value = { @ApiResponse(code = HttpStatus.SC_OK, message = UPDATE_QOS_POLICIES_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = UPDATE_QOS_POLICIES_HTTP_400_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@PostMapping(path = EXTENDED_QOS_UPDATE_POLICY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public QoSPolicyResponseDTO updateQoSPolicy(@RequestBody final QoSPolicyUpdateRequestDTO policy) {
		logger.debug("updateQoSPolicy requested...");

		long id = policy.getId();
		if (id < 1) {
			throw new BadPayloadException(ID_NOT_VALID_ERROR_MESSAGE);
		}
		if (extendedQoSDBService.getPolicy(id) == null) {
			throw new BadPayloadException("No policy could be found for id: " + id);
		}

		QoSPolicyResponseDTO updated = extendedQoSDBService.updateQoSPolicyById(id, policy);
		logger.debug("Updated policy with id '{}'", policy.getId());

		return updated;
	}

	/**
	 * Removes the desired QoS policy.
	 * 
	 * @param id Identifier of the policy to be deleted
	 */
	@ApiOperation(value = "Remove QoS policy by id", tags = { CoreCommonConstants.SWAGGER_TAG_MGMT })
	@ApiResponses(value = { @ApiResponse(code = HttpStatus.SC_OK, message = DELETE_QOS_POLICIES_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = DELETE_QOS_POLICIES_HTTP_400_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@DeleteMapping(path = EXTENDED_QOS_DELETE_POLICY)
	public void removeQoSPolicyById(@PathVariable(value = PATH_VARIABLE_ID) final long id) {
		logger.debug("removeQoSPolicyById requested...");

		if (id < 1) {
			throw new BadPayloadException(ID_NOT_VALID_ERROR_MESSAGE);
		}

		extendedQoSDBService.deleteQoSPolicyById(id);
		logger.debug("QoS Policy with id '{}' has been deleted.", id);
	}

	/**
	 * Returns the currently set global default QoS policy.
	 * 
	 * @return {@link QoSPOlicyResponseDTO}
	 */
	@ApiOperation(value = "Return global default QoS policy", response = QoSPolicyResponseDTO.class, tags = {
			CoreCommonConstants.SWAGGER_TAG_CLIENT })
	@ApiResponses(value = {
			@ApiResponse(code = HttpStatus.SC_OK, message = GET_GLOBAL_DEFAULT_QOS_POLICY_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@GetMapping(path = EXTENDED_QOS_GET_GLOBAL_DEFAULT_POLICY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public QoSPolicyResponseDTO getGlobalDefaultPolicy() {
		logger.debug("getGlobalDefaultPolicy requested...");
		return extendedQoSDBService.getGlobalDefaultPolicy();
	}

	/**
	 * Return the default policy that is currently set to the services with the
	 * requested id.
	 * 
	 * @param id Identifier of the system in question
	 * @response {@link QoSPolicyResponseDTO}
	 */
	@ApiOperation(value = "Return system default QoS policy for system id", response = QoSPolicyResponseDTO.class, tags = {
			CoreCommonConstants.SWAGGER_TAG_CLIENT })
	@ApiResponses(value = {
			@ApiResponse(code = HttpStatus.SC_OK, message = GET_SYSTEM_DEFAULT_QOS_POLICY_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@GetMapping(path = EXTENDED_QOS_GET_SYSTEM_DEFAULT_POLICY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public QoSPolicyResponseDTO getServiceDefaultPolicy(@PathVariable(value = PATH_VARIABLE_ID) final long id) {
		logger.debug("getServiceDefaultPolicy requested...");
		return extendedQoSDBService.getSystemDefaultPolicy(id);
	}

	/**
	 * Sets the global QoS policy.
	 * 
	 * @param id Identifier of the desired policy
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@ApiOperation(value = "Set global QoS policy", response = QoSPolicy.class, tags = {
			CoreCommonConstants.SWAGGER_TAG_MGMT })
	@ApiResponses(value = {
			@ApiResponse(code = HttpStatus.SC_OK, message = SET_GLOBAL_DEFAULT_QOS_POLICY_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = SET_GLOBAL_DEFAULT_QOS_POLICY_HTTP_400_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@GetMapping(path = EXTENDED_QOS_SET_GLOBAL_DEFAULT_POLICY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public QoSPolicyResponseDTO setGlobalDefaultPolicy(@PathVariable(value = PATH_VARIABLE_ID) final long id) {
		logger.debug("setGlobalDefaultPolicy requested...");

		if (id < 1) {
			throw new BadPayloadException("id must be greater than 0");
		}

		QoSPolicyResponseDTO created = extendedQoSDBService.setGlobalDefaultPolicy(id);
		logger.debug("Set global default policy to policy with id '{}'", created.getId());

		return created;
	}

	/**
	 * Sets the default policy for the specified system to the given policy.
	 * 
	 * @param systemId Identifier of the system
	 * @param policyId Identifier of the policy to set as default
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@ApiOperation(value = "Set global QoS policy", response = QoSPolicy.class, tags = {
			CoreCommonConstants.SWAGGER_TAG_MGMT })
	@ApiResponses(value = {
			@ApiResponse(code = HttpStatus.SC_OK, message = SET_SYSTEM_DEFAULT_QOS_POLICY_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = SET_SYSTEM_DEFAULT_QOS_POLICY_HTTP_400_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@GetMapping(path = EXTENDED_QOS_SET_SYSTEM_DEFAULT_POLICY, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public QoSPolicyResponseDTO setServiceDefaultPolicy(
			@PathVariable(value = PATH_VARIABLE_SYSTEM_ID) final long systemId,
			@PathVariable(value = PATH_VARIABLE_POLICY_ID) final long policyId) {
		logger.debug("setServiceDefaultPolicy requested...");

		if (systemId < 1) {
			throw new BadPayloadException("systemId must be greater than 0");
		}
		if (policyId < 1) {
			throw new BadPayloadException("policyId must be greater than 0");
		}

		QoSPolicyResponseDTO created = extendedQoSDBService.setSystemDefaultPolicy(systemId, policyId);
		logger.debug("Set default policy for system with id '{}' to policy with id '{}'", systemId, created.getId());

		return created;
	}

	/**
	 * Returns all available QoS evaluators that have been loaded on orchestrator
	 * startup.
	 * 
	 * @return {@link QoSEvaluatorListResponseDTO}
	 */
	@ApiOperation(value = "Return all available QoS evaluatos", response = QoSEvaluatorListResponseDTO.class, tags = {
			CoreCommonConstants.SWAGGER_TAG_MGMT })
	@ApiResponses(value = { @ApiResponse(code = HttpStatus.SC_OK, message = GET_QOS_EVALUATORS_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@GetMapping(path = EXTENDED_QOS_GET_EVALUATORS, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public QoSEvaluatorListResponseDTO getAvailableQoSEvaluators() {
		logger.debug("getAvailableQoSEvaluators requested...");
		return new QoSEvaluatorListResponseDTO(evaluatorLoader.getAvailableEvaluators());
	}
}

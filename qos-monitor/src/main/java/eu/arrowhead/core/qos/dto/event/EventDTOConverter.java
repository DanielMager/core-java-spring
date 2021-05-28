package eu.arrowhead.core.qos.dto.event;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.shared.EventDTO;
import eu.arrowhead.common.dto.shared.FinishedMonitoringMeasurementEventDTO;
import eu.arrowhead.common.dto.shared.IcmpPingResponseDTO;
import eu.arrowhead.common.dto.shared.InterruptedMonitoringMeasurementEventDTO;
import eu.arrowhead.common.dto.shared.QosMonitorEventType;
import eu.arrowhead.common.dto.shared.ReceivedMonitoringRequestEventDTO;
import eu.arrowhead.common.dto.shared.StartedMonitoringMeasurementEventDTO;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.core.qos.QosMonitorConstants;

public class EventDTOConverter {

	//=================================================================================================
	// members

	private static final Logger logger = LogManager.getLogger(EventDTOConverter.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	}

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static InterruptedMonitoringMeasurementEventDTO convertToInteruptedMonitoringMeasurementEvent(final EventDTO event) {
		logger.debug("convertToInteruptedMonitoringMeasurementEvent started...");

		Assert.notNull(event, "Event is null");
		Assert.isTrue(!Utilities.isEmpty(event.getPayload()), "Event payload is empty");
		Assert.isTrue(!Utilities.isEmpty(event.getTimeStamp()), "Event timeStamp is empty");

		final InterruptedMonitoringMeasurementEventDTO validEvent = new InterruptedMonitoringMeasurementEventDTO();
		validEvent.setEventType(QosMonitorEventType.INTERUPTED_MONITORING_MEASUREMENT);
		validEvent.setMetadata(event.getMetaData());
		validEvent.setPayload(event.getPayload());
		try {
			validEvent.setTimeStamp(Utilities.parseUTCStringToLocalZonedDateTime(event.getTimeStamp()));
		} catch (final DateTimeParseException ex) {

			throw new InvalidParameterException("Event timestap has invalid format.", ex);
		}

		validateInterruptedMonitoringMeasurementEventDTOFields(validEvent);

		return validEvent;
	}

	//-------------------------------------------------------------------------------------------------
	public static FinishedMonitoringMeasurementEventDTO convertToFinishedMonitoringMeasurementEvent(final EventDTO event) {
		logger.debug("convertToFinishedMonitoringMeasurementEvent started...");

		Assert.notNull(event, "Event is null");
		Assert.isTrue(!Utilities.isEmpty(event.getPayload()), "Event payload is empty");
		Assert.isTrue(!Utilities.isEmpty(event.getTimeStamp()), "Event timeStamp is empty");

		final FinishedMonitoringMeasurementEventDTO validEvent = new FinishedMonitoringMeasurementEventDTO();
		validEvent.setEventType(QosMonitorEventType.FINISHED_MONITORING_MEASUREMENT);
		validEvent.setMetadata(event.getMetaData());
		validEvent.setPayload(convertToIcmpPingResponse(event.getPayload()));
		try {
			validEvent.setTimeStamp(Utilities.parseUTCStringToLocalZonedDateTime(event.getTimeStamp()));
		} catch (final DateTimeParseException ex) {

			throw new InvalidParameterException("Event timestap has invalid format.", ex);
		}

		validateFinishedMonitoringMeasurementEventDTOFields(validEvent);

		return validEvent;
	}

	//-------------------------------------------------------------------------------------------------
	public static List<IcmpPingResponseDTO> convertToIcmpPingResponse(final String payload) {
		logger.debug("convertToIcmpPingResponse started...");

		Assert.notNull(payload, "Payload is null");
		Assert.isTrue(!Utilities.isEmpty(payload), "Payload is empty");

		try {
			final List<IcmpPingResponseDTO> validResponse = mapper.readValue(payload, new TypeReference<List<IcmpPingResponseDTO>>(){});

			return validResponse;

		} catch (final IOException e) {

			throw new InvalidParameterException("Invalid IcmpPingResponse");
		}
	}

	//-------------------------------------------------------------------------------------------------
	public static StartedMonitoringMeasurementEventDTO convertToStartedMonitoringMeasurementEvent(final EventDTO event) {
		logger.debug("convertToStartedMonitoringMeasurementEvent started...");

		Assert.notNull(event, "Event is null");
		Assert.isTrue(!Utilities.isEmpty(event.getPayload()), "Event payload is empty");
		Assert.isTrue(!Utilities.isEmpty(event.getTimeStamp()), "Event timeStamp is empty");

		final StartedMonitoringMeasurementEventDTO validEvent = new StartedMonitoringMeasurementEventDTO();
		validEvent.setEventType(QosMonitorEventType.STARTED_MONITORING_MEASUREMENT);
		validEvent.setMetadata(event.getMetaData());
		validEvent.setPayload(event.getPayload());
		try {
			validEvent.setTimeStamp(Utilities.parseUTCStringToLocalZonedDateTime(event.getTimeStamp()));
		} catch (final DateTimeParseException ex) {

			throw new InvalidParameterException("Event timestap has invalid format.", ex);
		}

		validateStartedMonitoringMeasurementEventDTOFields(validEvent);

		return validEvent;
	}

	//-------------------------------------------------------------------------------------------------
	public static ReceivedMonitoringRequestEventDTO convertToReceivedMonitoringRequestEvent(final EventDTO event) {
		logger.debug("convertToReceivedMonitoringRequestEvent started...");

		Assert.notNull(event, "Event is null");
		Assert.isTrue(!Utilities.isEmpty(event.getPayload()), "Event payload is empty");
		Assert.isTrue(!Utilities.isEmpty(event.getTimeStamp()), "Event timeStamp is empty");

		final ReceivedMonitoringRequestEventDTO validEvent = new ReceivedMonitoringRequestEventDTO();
		validEvent.setEventType(QosMonitorEventType.RECEIVED_MONITORING_REQUEST);
		validEvent.setMetadata(event.getMetaData());
		validEvent.setPayload(event.getPayload());
		try {
			validEvent.setTimeStamp(Utilities.parseUTCStringToLocalZonedDateTime(event.getTimeStamp()));
		} catch (final DateTimeParseException ex) {

			throw new InvalidParameterException("Event timestap has invalid format.", ex);
		}

		validateReceivedMonitoringRequestEventDTOFields(validEvent);

		return validEvent;
	}

	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private static void validateFinishedMonitoringMeasurementEventDTOFields(final FinishedMonitoringMeasurementEventDTO event) {

		Assert.isTrue(event.getEventType().equals(QosMonitorEventType.FINISHED_MONITORING_MEASUREMENT), "Event type must be: FINISHED_MONITORING_MEASUREMENT");
		Assert.isTrue(event.getMetadata().containsKey(QosMonitorConstants.PROCESS_ID_KEY), "Meta data must contain: " + QosMonitorConstants.PROCESS_ID_KEY);
		Assert.isTrue(event.getMetadata().keySet().size() == QosMonitorConstants.FINISHED_MONITORING_MEASUREMENT_EVENT_PAYLOAD_METADATA_SIZE, "Meta data keys quantity is not valid");
	}

	//-------------------------------------------------------------------------------------------------
	private static void validateInterruptedMonitoringMeasurementEventDTOFields(final InterruptedMonitoringMeasurementEventDTO event) {

		Assert.isTrue(event.getEventType().equals(QosMonitorEventType.INTERUPTED_MONITORING_MEASUREMENT), "Event type must be: INTERUPTED_MONITORING_MEASUREMENT");
		//Assert.isTrue(event.getPayload().equalsIgnoreCase(QosMonitorConstants.INTERRUPTED_MONITORING_MEASUREMENT_EVENT_PAYLOAD_SCHEMA), "Payload must be: " + QosMonitorConstants.INTERRUPTED_MONITORING_MEASUREMENT_EVENT_PAYLOAD_SCHEMA);
		Assert.isTrue(event.getMetadata().containsKey(QosMonitorConstants.PROCESS_ID_KEY), "Meta data must contain: " + QosMonitorConstants.PROCESS_ID_KEY);
		Assert.isTrue( (event.getMetadata().keySet().size() <= QosMonitorConstants.INTERRUPTED_MONITORING_MEASUREMENT_EVENT_PAYLOAD_METADATA_MAX_SIZE ), "Meta data has more keys than allowed");

	}

	//-------------------------------------------------------------------------------------------------
	private static void validateReceivedMonitoringRequestEventDTOFields(final ReceivedMonitoringRequestEventDTO event) {

		Assert.isTrue(event.getEventType().equals(QosMonitorEventType.RECEIVED_MONITORING_REQUEST), "Event type must be: RECEIVED_MONITORING_REQUEST");
		//Assert.isTrue(event.getPayload().equalsIgnoreCase(QosMonitorConstants.RECEIVED_MONITORING_REQUEST_EVENT_PAYLOAD_SCHEMA), "Payload must be: " + QosMonitorConstants.RECEIVED_MONITORING_REQUEST_EVENT_PAYLOAD_SCHEMA);
		Assert.isTrue(event.getMetadata().containsKey(QosMonitorConstants.PROCESS_ID_KEY), "Meta data must contain: " + QosMonitorConstants.PROCESS_ID_KEY);
		Assert.isTrue(event.getMetadata().keySet().size() == QosMonitorConstants.RECEIVED_MONITORING_REQUEST_EVENT_PAYLOAD_METADATA_SIZE, "Meta data keys quantity is not valid");
	}

	//-------------------------------------------------------------------------------------------------
	private static void validateStartedMonitoringMeasurementEventDTOFields(final StartedMonitoringMeasurementEventDTO event) {

		Assert.isTrue(event.getEventType().equals(QosMonitorEventType.STARTED_MONITORING_MEASUREMENT), "Event type must be: " + QosMonitorEventType.STARTED_MONITORING_MEASUREMENT.name());
		//Assert.isTrue(event.getPayload().equalsIgnoreCase(QosMonitorConstants.STARTED_MONITORING_MEASUREMENT_EVENT_PAYLOAD_SCHEMA), "Payload must be: " + QosMonitorConstants.STARTED_MONITORING_MEASUREMENT_EVENT_PAYLOAD_SCHEMA);
		Assert.isTrue(event.getMetadata().containsKey(QosMonitorConstants.PROCESS_ID_KEY), "Meta data must contain: " + QosMonitorConstants.PROCESS_ID_KEY);
		Assert.isTrue(event.getMetadata().keySet().size() == QosMonitorConstants.STARTED_MONITORING_MEASUREMENT_EVENT_PAYLOAD_METADATA_SIZE, "Meta data keys quantity is not valid");
	}

	//-------------------------------------------------------------------------------------------------
	private EventDTOConverter() {
		throw new UnsupportedOperationException();
	}
}

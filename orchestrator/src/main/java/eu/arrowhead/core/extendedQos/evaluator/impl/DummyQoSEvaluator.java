package eu.arrowhead.core.extendedQos.evaluator.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;
import eu.arrowhead.core.extendedQos.evaluator.QoSEvaluator;

/**
 * Dummy implementation for a {@link QoSEvaluator} that will be even loaded when
 * Arrowhead has been started from code. It does not process the service list in
 * any way, it just returns it.
 */
@Component
public class DummyQoSEvaluator implements QoSEvaluator {

	protected Logger logger = LogManager.getLogger(DummyQoSEvaluator.class);

	@Override
	public List<OrchestrationResultDTO> evaluateServices(List<OrchestrationResultDTO> availableServices,
			Map<String, String> properties) {
		logger.debug("dummyQoSEvaluator: evaluateServices started...");
		return availableServices;
	}

	@Override
	public String getName() {
		return "dummyQoSEvaluator";
	}

	@Override
	public String getDescription() {
		return "Dummy QoS evaluator that does not change anything";
	}

	@Override
	public Map<String, String> getExampleParameters() {
		return new HashMap<>();
	}
}

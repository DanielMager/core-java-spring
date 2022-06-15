package eu.arrowhead.core.extendedQos.evaluator;

import java.util.List;
import java.util.Map;

import eu.arrowhead.common.dto.shared.OrchestrationResultDTO;

/**
 * Interface describing a QoS evaluator.
 */
public interface QoSEvaluator {

	/**
	 * Processes the input services using given properties, sorting and/or filtering
	 * and returning them.
	 * 
	 * @param availableServices possible services to be evaluated
	 * @param properties        Map of properties used by the evaluator
	 * @return processed list of services
	 */
	public List<OrchestrationResultDTO> evaluateServices(List<OrchestrationResultDTO> availableServices,
			Map<String, String> properties);

	/**
	 * Return the name of the QoS evaluator
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * Return the description of the QoS evaluator
	 * 
	 * @return description
	 */
	public String getDescription();

	/**
	 * Gets example parameters, this evaluator processes. Note, that these are only
	 * information, not actually parameters used!
	 * 
	 * @return Map of example parameters
	 */
	public Map<String, String> getExampleParameters();
}
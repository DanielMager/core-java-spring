package eu.arrowhead.core.extendedQos.evaluator;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.dto.shared.QoSEvaluatorResponseDTO;

/**
 * Service for loading QoS evaluators that are scanned during system startup.
 */
@Service
public class QoSEvaluatorLoader {

	List<QoSEvaluator> evaluators;

	protected Logger logger = LogManager.getLogger(QoSEvaluatorLoader.class);

	/**
	 * Constructor.
	 */
	public QoSEvaluatorLoader(List<QoSEvaluator> evaluators) {
		this.evaluators = evaluators;
	}

	/**
	 * Returns all available QoS evaluators.
	 * 
	 * @return {@link List} of {@link QoSEvaluatorResponseDTO}
	 */
	public List<QoSEvaluatorResponseDTO> getAvailableEvaluators() {
		logger.debug("getAvailableEvaluators started..:");
		return this.evaluators.stream().map(evaluator -> new QoSEvaluatorResponseDTO(evaluator.getName(),
				evaluator.getDescription(), evaluator.getExampleParameters())).collect(Collectors.toList());
	}

	/**
	 * Returns an instance of the requested QoS evaluator.
	 * 
	 * @param name Name of the desired QoS evaluator
	 * @return {@link QoSEvaluator}
	 */
	public QoSEvaluator getEvaluatorByName(String name) {
		logger.debug("getEvaluatorByName started...");
		List<QoSEvaluator> foundEvaluators = evaluators.stream().filter(evaluator -> evaluator.getName().equals(name))
				.collect(Collectors.toList());
		if (foundEvaluators.size() > 0) {
			logger.debug("Found {} evaluators with name '{}'", foundEvaluators.size(), name);
			return foundEvaluators.get(0);
		}
		return null;
	}
}

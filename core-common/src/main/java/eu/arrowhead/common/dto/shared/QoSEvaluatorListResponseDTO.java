package eu.arrowhead.common.dto.shared;

import java.io.Serializable;
import java.util.List;

public class QoSEvaluatorListResponseDTO implements Serializable {

	private static final long serialVersionUID = 4380670029581124848L;

	// =================================================================================================
	// members

	List<QoSEvaluatorResponseDTO> qosEvaluators;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public QoSEvaluatorListResponseDTO() {
	}

	// -------------------------------------------------------------------------------------------------
	public QoSEvaluatorListResponseDTO(final List<QoSEvaluatorResponseDTO> evaluators) {
		this.qosEvaluators = evaluators;
	}

	public List<QoSEvaluatorResponseDTO> getEvaluators() {
		return qosEvaluators;
	}

	// -------------------------------------------------------------------------------------------------
	
	public void setEvaluators(final List<QoSEvaluatorResponseDTO> evaluators) {
		this.qosEvaluators = evaluators;
	}
}
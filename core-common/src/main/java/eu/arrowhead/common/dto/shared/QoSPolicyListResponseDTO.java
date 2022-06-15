package eu.arrowhead.common.dto.shared;

import java.io.Serializable;
import java.util.List;

import eu.arrowhead.common.database.entity.QoSPolicy;

public class QoSPolicyListResponseDTO implements Serializable {

	private static final long serialVersionUID = 4380670029581124848L;

	// =================================================================================================
	// members

	private List<QoSPolicy> qosPolicies;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public QoSPolicyListResponseDTO() {
	}

	// -------------------------------------------------------------------------------------------------
	public QoSPolicyListResponseDTO(final List<QoSPolicy> policies) {
		this.qosPolicies = policies;
	}

	// -------------------------------------------------------------------------------------------------
	public List<QoSPolicy> getPolicies() {
		return qosPolicies;
	}

	// -------------------------------------------------------------------------------------------------
	public void setPolicies(final List<QoSPolicy> policies) {
		this.qosPolicies = policies;
	}
}
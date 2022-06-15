package eu.arrowhead.common.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "global_default_qos_policy")
public class GlobalDefaultQoSPolicy {

	// =================================================================================================
	// members

	@Id
	private long id;

	@Column(name = "policy_id", nullable = true)
	private long defaultPolicyId;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public GlobalDefaultQoSPolicy() {
		this.id = 1;
	}
	
	public GlobalDefaultQoSPolicy(long defaultPolicyId) {
		this.id = 1;
		this.defaultPolicyId = defaultPolicyId;
	}

	// -------------------------------------------------------------------------------------------------

	public long getId() {
		return id;
	}

	public long getDefaultPolicyId() {
		return defaultPolicyId;
	}

	// -------------------------------------------------------------------------------------------------

	public void setId(long id) {
		// noop
	}

	public void setDefaultPolicyId(long defaultPolicyId) {
		this.defaultPolicyId = defaultPolicyId;
	}

}

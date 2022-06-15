package eu.arrowhead.common.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "system_default_qos_policies")
public class SystemDefaultQoSPolicy {

	// =================================================================================================
	// members

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "system_id", nullable = false)
	private long systemId;

	@Column(name = "policy_id", nullable = false)
	private long defaultPolicyId;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public SystemDefaultQoSPolicy() {
	}

	// -------------------------------------------------------------------------------------------------
	public SystemDefaultQoSPolicy(final long systemId, final long defaultPolicyId) {
		this.systemId = systemId;
		this.defaultPolicyId = defaultPolicyId;
	}

	// -------------------------------------------------------------------------------------------------

	public long getId() {
		return id;
	}

	public long getSystemId() {
		return systemId;
	}

	public long getDefaultPolicyId() {
		return defaultPolicyId;
	}

	// -------------------------------------------------------------------------------------------------

	public void setId(final long id) {
		this.id = id;
	}

	public void setSystemId(final long systemId) {
		this.systemId = systemId;
	}

	public void setDefaultPolicyId(final long defaultPolicyId) {
		this.defaultPolicyId = defaultPolicyId;
	}
}

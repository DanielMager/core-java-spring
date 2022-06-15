package eu.arrowhead.common.dto.shared;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;

import eu.arrowhead.common.database.entity.QoSPolicy;

public class QoSPolicyResponseDTO implements Serializable {

	// =================================================================================================
	// members

	private static final long serialVersionUID = 3437941596295465836L;

	private long id;
	private String name;
	private String description;
	private String evaluator;
	private Map<String, String> parameters;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public QoSPolicyResponseDTO() {
	}

	// -------------------------------------------------------------------------------------------------
	public QoSPolicyResponseDTO(QoSPolicy policy) {
		this.id = policy.getId();
		this.name = policy.getName();
		this.description = policy.getDescription();
		this.evaluator = policy.getEvaluator();
		this.parameters = policy.getParameters();
		this.createdAt = policy.getCreatedAt();
		this.updatedAt = policy.getUpdatedAt();
	}

	// -------------------------------------------------------------------------------------------------

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getEvaluator() {
		return evaluator;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}
}

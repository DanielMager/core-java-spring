package eu.arrowhead.common.dto.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class QoSPolicyCreateRequestDTO implements Serializable {

	// =================================================================================================
	// members

	private static final long serialVersionUID = 3437941596295465836L;

	private String name;
	private String description;
	private String evaluator;
	private Map<String, String> parameters;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public QoSPolicyCreateRequestDTO() {
		// noop
	}

	// -------------------------------------------------------------------------------------------------
	public QoSPolicyCreateRequestDTO(String name, String description, String evaluator,
			Map<String, String> parameters) {
		this.name = name;
		this.description = description;
		this.evaluator = evaluator;
		this.parameters = parameters;
	}

	// -------------------------------------------------------------------------------------------------

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

	// -------------------------------------------------------------------------------------------------

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String parameterKey, String parameterValue) {
		if (parameters == null) {
			parameters = new HashMap<>();
		}
		parameters.put(parameterKey, parameterValue);
	}
}

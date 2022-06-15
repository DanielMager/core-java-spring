package eu.arrowhead.common.dto.shared;

import java.io.Serializable;
import java.util.Map;

public class QoSEvaluatorResponseDTO implements Serializable {

	// =================================================================================================
	// members

	private static final long serialVersionUID = 892203800461122446L;

	private String name;
	private String description;
	private Map<String, String> exampleParameters;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public QoSEvaluatorResponseDTO() {
	}

	// -------------------------------------------------------------------------------------------------
	public QoSEvaluatorResponseDTO(String name, String description, Map<String, String> exampleParameters) {
		this.name = name;
		this.description = description;
		this.exampleParameters = exampleParameters;
	}

	// -------------------------------------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, String> getExampleParameters() {
		return exampleParameters;
	}

	// -------------------------------------------------------------------------------------------------

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setExampleParameters(Map<String, String> exampleParameters) {
		this.exampleParameters = exampleParameters;
	}
}

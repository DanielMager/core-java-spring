package eu.arrowhead.common.dto.shared;

import java.io.Serializable;

public class ReputationGetRatingRequestDTO implements Serializable {

	// =================================================================================================
	// members

	private static final long serialVersionUID = 4763703644539090427L;
	
	private String serviceDefinition;

	private String providerName;
	private String providerAddress;
	private int providerPort;
	
	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public ReputationGetRatingRequestDTO() {
	}

	// -------------------------------------------------------------------------------------------------

	public String getServiceDefinition() {
		return serviceDefinition;
	}

	public String getProviderName() {
		return providerName;
	}

	public String getProviderAddress() {
		return providerAddress;
	}

	public int getProviderPort() {
		return providerPort;
	}

	// -------------------------------------------------------------------------------------------------

	public void setServiceDefinition(String serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public void setProviderAddress(String providerAddress) {
		this.providerAddress = providerAddress;
	}

	public void setProviderPort(int providerPort) {
		this.providerPort = providerPort;
	}
}

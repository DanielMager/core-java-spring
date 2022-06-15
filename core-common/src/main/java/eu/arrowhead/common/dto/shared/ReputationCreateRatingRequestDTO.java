package eu.arrowhead.common.dto.shared;

import java.io.Serializable;

public class ReputationCreateRatingRequestDTO implements Serializable {

	// =================================================================================================
	// members

	private static final long serialVersionUID = 6401317104934367899L;

	private String serviceDefinition;

	private String providerName;
	private String providerAddress;
	private int providerPort;
	
	private String consumerName;
	private String consumerAddress;
	private int consumerPort;
	
	private int rating;
	
	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public ReputationCreateRatingRequestDTO() {
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

	public String getConsumerName() {
		return consumerName;
	}

	public String getConsumerAddress() {
		return consumerAddress;
	}

	public int getConsumerPort() {
		return consumerPort;
	}

	public int getRating() {
		return rating;
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

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	public void setConsumerAddress(String consumerAddress) {
		this.consumerAddress = consumerAddress;
	}

	public void setConsumerPort(int consumerPort) {
		this.consumerPort = consumerPort;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

}

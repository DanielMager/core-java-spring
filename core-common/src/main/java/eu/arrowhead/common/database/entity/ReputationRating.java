package eu.arrowhead.common.database.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "reputation_rating", uniqueConstraints = @UniqueConstraint(columnNames = { "service_id", "provider_id", "consumer_id"}))
public class ReputationRating {
	// =================================================================================================
	// members

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "service_id", nullable = false)
	private long serviceId;

	@Column(name = "provider_id", nullable = false)
	private long providerId;

	@Column(name = "consumer_id", nullable = false)
	private long consumerId;

	@Column(nullable = false)
	private int rating;

	@Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private ZonedDateTime updatedAt;

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public ReputationRating() {
	}

	// -------------------------------------------------------------------------------------------------
	public ReputationRating(long serviceId, long providerId, long consumerId, int rating) {
		super();
		this.serviceId = serviceId;
		this.providerId = providerId;
		this.consumerId = consumerId;
		this.rating = rating;
	}

	// -------------------------------------------------------------------------------------------------
	@PrePersist
	public void onCreate() {
		this.updatedAt = ZonedDateTime.now();
	}

	// -------------------------------------------------------------------------------------------------
	@PreUpdate
	public void onUpdate() {
		this.updatedAt = ZonedDateTime.now();
	}

	// -------------------------------------------------------------------------------------------------
	
	public long getId() {
		return id;
	}

	public long getServiceId() {
		return serviceId;
	}

	public long getProviderId() {
		return providerId;
	}

	public long getConsumerId() {
		return consumerId;
	}

	public int getRating() {
		return rating;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}
	
	// -------------------------------------------------------------------------------------------------
	
	public void setId(long id) {
		this.id = id;
	}

	public void setServiceDefinition(long serviceId) {
		this.serviceId = serviceId;
	}

	public void setProviderId(long providerId) {
		this.providerId = providerId;
	}

	public void setConsumerId(long consumerId) {
		this.consumerId = consumerId;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	
}

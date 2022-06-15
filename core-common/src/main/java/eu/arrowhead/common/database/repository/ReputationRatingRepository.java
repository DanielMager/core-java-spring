package eu.arrowhead.common.database.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import eu.arrowhead.common.database.entity.ReputationRating;

@Repository
public interface ReputationRatingRepository extends RefreshableRepository<ReputationRating, Long> {

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public List<ReputationRating> findAllByServiceIdAndProviderId(long serviceId, long providerId);
	public Optional<ReputationRating> findByServiceIdAndProviderIdAndConsumerId(long serviceId, long providerId, long consumerId);
}

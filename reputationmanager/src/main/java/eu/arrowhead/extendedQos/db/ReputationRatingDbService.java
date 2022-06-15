package eu.arrowhead.extendedQos.db;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.database.entity.ReputationRating;
import eu.arrowhead.common.database.repository.ReputationRatingRepository;
import eu.arrowhead.common.exception.ArrowheadException;

/**
 * Service for dealing with reputation ratings in the database.
 */
@Service
public class ReputationRatingDbService {

	@Autowired
	ReputationRatingRepository ratingRepository;

	protected Logger logger = LogManager.getLogger(ReputationRatingDbService.class);

	/**
	 * Returns all ratings for the given service.
	 * 
	 * @param serviceId  Identifier of the desired service
	 * @param providerId Identifier of the provider that provides the desired
	 *                   service
	 * @return {@link List} of {@link ReputationRating}
	 */
	public List<ReputationRating> getRatings(long serviceId, long providerId) {
		logger.debug("getRatings started...");

		return ratingRepository.findAllByServiceIdAndProviderId(serviceId, providerId);
	}

	/**
	 * Creates (or updates) a rating using the given parameters.
	 * 
	 * @param serviceId  Identifier of desired service
	 * @param providerId Identifier of the provider that provides the desired
	 *                   service
	 * @param rating     the value of this rating
	 */
	public void createOrUpdateRating(long serviceId, long providerId, long consumerId, int rating) {
		logger.debug("createOrUpdateRating started...");

		ReputationRating saveRating;
		Optional<ReputationRating> foundRating = ratingRepository.findByServiceIdAndProviderIdAndConsumerId(serviceId,
				providerId, consumerId);
		if (foundRating.isPresent()) {
			saveRating = foundRating.get();
			saveRating.setRating(rating);
		} else {
			saveRating = new ReputationRating(serviceId, providerId, consumerId, rating);
		}

		try {
			ratingRepository.saveAndFlush(saveRating);
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ArrowheadException(CoreCommonConstants.DATABASE_OPERATION_EXCEPTION_MSG);
		}
	}
}

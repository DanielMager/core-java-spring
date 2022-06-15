package eu.arrowhead.extendedQos;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.Defaults;
import eu.arrowhead.common.database.entity.ReputationRating;
import eu.arrowhead.common.database.entity.ServiceDefinition;
import eu.arrowhead.common.database.entity.System;
import eu.arrowhead.common.database.repository.ServiceDefinitionRepository;
import eu.arrowhead.common.database.repository.SystemRepository;
import eu.arrowhead.common.dto.shared.ReputationCreateRatingRequestDTO;
import eu.arrowhead.common.dto.shared.ReputationGetRatingRequestDTO;
import eu.arrowhead.common.exception.BadPayloadException;
import eu.arrowhead.extendedQos.db.ReputationRatingDbService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST controller class for dealing with the Reputation Manager, giving and
 * retrieving feedback about services.
 */
@Api(tags = { CoreCommonConstants.SWAGGER_TAG_ALL })
@CrossOrigin(maxAge = Defaults.CORS_MAX_AGE, allowCredentials = Defaults.CORS_ALLOW_CREDENTIALS, allowedHeaders = {
		HttpHeaders.ORIGIN, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, HttpHeaders.AUTHORIZATION })
@RestController
@RequestMapping(CommonConstants.REPUTATIONMANAGER_URI)
public class ReputationManagerController {

	private static final String GET_RATING_URI = CommonConstants.REPUTATIONMANAGER_URI;
	private static final String CREATE_RATING_URI = CommonConstants.OP_REPUTATIONMANAGER_RATE;

	private static final String GET_RATING_HTTP_200_MESSAGE = "Rating for requested service returned";

	private static final String CREATE_RATING_HTTP_200_MESSAGE = "Rating created for requested parameters";
	private static final String CREATE_RATING_HTTP_400_MESSAGE = "Creation of rating failed for requested parameters";

	@Value(ReputationManagerConstants.$INCLUSION_FACTOR)
	private float inclusionFactor;

	@Autowired
	private ReputationRatingDbService ratingDbService;

	@Autowired
	private SystemRepository systemRepository;

	@Autowired
	private ServiceDefinitionRepository serviceRepository;

	private final Logger logger = LogManager.getLogger(ReputationManagerController.class);

	/**
	 * Returns the rating for the requested service/system.
	 * 
	 * @param request {@link ReputationRatingRequestDTO}
	 * @return rating of this system/service
	 */
	@ApiOperation(value = "Return the rating for a provider's specific service", response = Float.class, tags = {
			CoreCommonConstants.SWAGGER_TAG_CLIENT })
	@ApiResponses(value = { @ApiResponse(code = HttpStatus.SC_OK, message = GET_RATING_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@PostMapping(path = GET_RATING_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Float getRating(@RequestBody ReputationGetRatingRequestDTO request) {
		logger.debug("getRating started ...");

		Optional<ServiceDefinition> serviceDefinition = serviceRepository
				.findByServiceDefinition(request.getServiceDefinition());
		if (serviceDefinition.isEmpty()) {
			throw new BadPayloadException(
					String.format("service definition %s not found", request.getServiceDefinition()));
		}

		Optional<System> providerSystem = systemRepository.findBySystemNameAndAddressAndPort(request.getProviderName(),
				request.getProviderAddress(), request.getProviderPort());
		if (providerSystem.isEmpty()) {
			throw new BadPayloadException(String.format("provider system %s (%s:%d) not found",
					request.getProviderName(), request.getProviderAddress(), request.getProviderPort()));
		}

		List<ReputationRating> ratings = ratingDbService.getRatings(serviceDefinition.get().getId(),
				providerSystem.get().getId());
		float reputationScore = 0f;

		for (int i = 0; i < ratings.size(); i++) {
			int rating = ratings.get(i).getRating();
			long ratingAge = ChronoUnit.MINUTES.between(ratings.get(i).getUpdatedAt(), ZonedDateTime.now());
			reputationScore += rating * Math.pow(inclusionFactor, ratingAge);
		}

		return reputationScore;
	}

	/**
	 * Creates a rating for the requested service.
	 * 
	 * @param request {@link ReputationCreateRatingRequestDTO}
	 */
	@ApiOperation(value = "Rate the specified service from the specified provider", tags = {
			CoreCommonConstants.SWAGGER_TAG_CLIENT })
	@ApiResponses(value = { @ApiResponse(code = HttpStatus.SC_OK, message = CREATE_RATING_HTTP_200_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = CREATE_RATING_HTTP_400_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = CoreCommonConstants.SWAGGER_HTTP_401_MESSAGE),
			@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = CoreCommonConstants.SWAGGER_HTTP_500_MESSAGE) })
	@PostMapping(path = CREATE_RATING_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public void createRating(@RequestBody ReputationCreateRatingRequestDTO request) {
		logger.info("createRating started ...");

		Optional<ServiceDefinition> serviceDefinition = serviceRepository
				.findByServiceDefinition(request.getServiceDefinition());
		if (serviceDefinition.isEmpty()) {
			throw new BadPayloadException(
					String.format("service definition %s not found", request.getServiceDefinition()));
		}

		Optional<System> providerSystem = systemRepository.findBySystemNameAndAddressAndPort(request.getProviderName(),
				request.getProviderAddress(), request.getProviderPort());
		if (providerSystem.isEmpty()) {
			throw new BadPayloadException(String.format("provider %s (%s:%d) not found", request.getProviderName(),
					request.getProviderAddress(), request.getProviderPort()));
		}

		Optional<System> consumerSystem = systemRepository.findBySystemNameAndAddressAndPort(request.getConsumerName(),
				request.getConsumerAddress(), request.getConsumerPort());
		if (consumerSystem.isEmpty()) {
			throw new BadPayloadException(String.format("provider %s (%s:%d) not found", request.getConsumerName(),
					request.getConsumerAddress(), request.getConsumerPort()));
		}

		ratingDbService.createOrUpdateRating(serviceDefinition.get().getId(), providerSystem.get().getId(),
				consumerSystem.get().getId(), request.getRating());
	}
}

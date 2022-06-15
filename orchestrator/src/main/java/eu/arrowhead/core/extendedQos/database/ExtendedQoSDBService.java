package eu.arrowhead.core.extendedQos.database;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.database.entity.GlobalDefaultQoSPolicy;
import eu.arrowhead.common.database.entity.QoSPolicy;
import eu.arrowhead.common.database.entity.System;
import eu.arrowhead.common.database.entity.SystemDefaultQoSPolicy;
import eu.arrowhead.common.database.repository.GlobalDefaultQoSPolicyRepository;
import eu.arrowhead.common.database.repository.QoSPolicyRepository;
import eu.arrowhead.common.database.repository.SystemDefaultQoSPolicyRepository;
import eu.arrowhead.common.database.repository.SystemRepository;
import eu.arrowhead.common.dto.shared.QoSPolicyCreateRequestDTO;
import eu.arrowhead.common.dto.shared.QoSPolicyResponseDTO;
import eu.arrowhead.common.dto.shared.QoSPolicyUpdateRequestDTO;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;

/**
 * Service for working with the database in the context of QoS policies.
 */
@Service
public class ExtendedQoSDBService {

	private static final long GLOBAL_DEFAULT_POLICY_ID = 1;

	@Autowired
	QoSPolicyRepository policyRepository;

	@Autowired
	GlobalDefaultQoSPolicyRepository globalDefaultPolicyRepository;

	@Autowired
	SystemDefaultQoSPolicyRepository systemDefaultPolicyRepository;

	@Autowired
	SystemRepository systemRepository;

	protected Logger logger = LogManager.getLogger(ExtendedQoSDBService.class);

	/**
	 * Returns all QoS policies from the database.
	 * 
	 * @return {@link List} of {@link QoSPolicy}
	 */
	public List<QoSPolicy> getAllPolicies() {
		return policyRepository.findAll();
	}

	/**
	 * Returns the QoS policy with the specified id.
	 * 
	 * @param id Identifier of the desired QoS policy
	 * @return {@link QoSPOlicyResponseDTO}
	 */
	public QoSPolicyResponseDTO getPolicy(final long id) {
		logger.debug("getPolicy(id) started...");

		QoSPolicyResponseDTO response = new QoSPolicyResponseDTO();
		Optional<QoSPolicy> policy = policyRepository.findById(id);
		if (policy.isPresent()) {
			response = new QoSPolicyResponseDTO(policy.get());
		}

		return response;
	}

	/**
	 * Returns the QoS policy specified by the name.
	 * 
	 * @param policyName name of the desired policy
	 * @return {@link QoSPOlicyResponseDTO}
	 */
	public QoSPolicyResponseDTO getPolicy(final String policyName) {
		logger.debug("getPolicy(policyName) started...");

		QoSPolicyResponseDTO response = new QoSPolicyResponseDTO();
		Optional<QoSPolicy> policy = policyRepository.findByName(policyName);
		if (policy.isPresent()) {
			response = new QoSPolicyResponseDTO(policy.get());
		}

		return response;
	}

	/**
	 * Creates the policy given as parameter.
	 * 
	 * @param policy {@link QoSPolicyCreateRequestDTO}
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@Transactional(rollbackFor = ArrowheadException.class)
	public QoSPolicyResponseDTO createQoSPolicy(final QoSPolicyCreateRequestDTO policy) {
		logger.debug("createQoSPolicy started...");

		if (policy == null) {
			throw new InvalidParameterException("policy is null");
		}

		QoSPolicy newPolicy = new QoSPolicy(policy.getName(), policy.getDescription(), policy.getEvaluator(),
				policy.getParameters());

		QoSPolicyResponseDTO created = null;
		try {
			created = new QoSPolicyResponseDTO(policyRepository.saveAndFlush(newPolicy));
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ArrowheadException(CoreCommonConstants.DATABASE_OPERATION_EXCEPTION_MSG);
		}

		return created;
	}

	/**
	 * Updates the QoS policy in the system specified by identifier with the given
	 * values.
	 * 
	 * @param id     Identifier of the QoS policy to be updated
	 * @param policy {@link QoSPolicyUpdateRequestDTO}
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@Transactional(rollbackFor = ArrowheadException.class)
	public QoSPolicyResponseDTO updateQoSPolicyById(final long id, final QoSPolicyUpdateRequestDTO policy) {
		logger.debug("updateQoSPolicy started...");

		if (id < 1) {
			throw new InvalidParameterException("id must be greater than 0");
		}
		if (policy == null) {
			throw new InvalidParameterException("policy is null");
		}

		QoSPolicyResponseDTO updated = null;
		QoSPolicy existingPolicy = policyRepository.findById(id).orElse(null);
		if (existingPolicy != null) {
			existingPolicy.setName(policy.getName());
			existingPolicy.setDescription(policy.getDescription());
			existingPolicy.setEvaluator(policy.getEvaluator());
			existingPolicy.setParameters(policy.getParameters());

			try {
				updated = new QoSPolicyResponseDTO(policyRepository.saveAndFlush(existingPolicy));
			} catch (Exception e) {
				logger.debug(e.getMessage(), e);
				throw new ArrowheadException(CoreCommonConstants.DATABASE_OPERATION_EXCEPTION_MSG);
			}
		}

		return updated;
	}

	/**
	 * Deletes the policy specified by the identifier.
	 * 
	 * @param id Identifier of the policy to be deleted
	 */
	@Transactional(rollbackFor = ArrowheadException.class)
	public void deleteQoSPolicyById(final long id) {
		logger.debug("deleteQoSPolicyById started...");

		try {
			if (policyRepository.existsById(id)) {
				policyRepository.deleteById(id);
				policyRepository.flush();
			}
		} catch (final Exception ex) {
			logger.debug(ex.getMessage(), ex);
			throw new ArrowheadException(CoreCommonConstants.DATABASE_OPERATION_EXCEPTION_MSG);
		}
	}

	/**
	 * Returns the currently set global default QoS policy.
	 * 
	 * @return {@link QoSPOlicyResponseDTO}
	 */
	@Transactional(rollbackFor = ArrowheadException.class)
	public QoSPolicyResponseDTO getGlobalDefaultPolicy() {
		logger.debug("getGlobalDefaultPolicy started...");

		QoSPolicyResponseDTO response = new QoSPolicyResponseDTO();
		Optional<GlobalDefaultQoSPolicy> globalDefaultPolicy = globalDefaultPolicyRepository
				.findById(GLOBAL_DEFAULT_POLICY_ID);
		if (globalDefaultPolicy.isPresent()) {
			Optional<QoSPolicy> foundPolicy = policyRepository.findById(globalDefaultPolicy.get().getDefaultPolicyId());
			if (foundPolicy.isPresent()) {
				response = new QoSPolicyResponseDTO(foundPolicy.get());
			} else {
				logger.warn("invalid policy id '{}' for global default policy",
						globalDefaultPolicy.get().getDefaultPolicyId());
			}
		} else {
			logger.debug("no global qos policy set");
		}

		return response;
	}

	/**
	 * Sets the global default QoS policy.
	 * 
	 * @param policyId Identifier of the policy to be set as global default
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@Transactional(rollbackFor = ArrowheadException.class)
	public QoSPolicyResponseDTO setGlobalDefaultPolicy(final long policyId) {
		logger.debug("setGlobalDefaultPolicy started...");

		if (!policyRepository.existsById(policyId)) {
			throw new InvalidParameterException("qos policy with id '" + policyId + "' does not exist");
		}

		QoSPolicyResponseDTO newDefault = new QoSPolicyResponseDTO();
		try {
			GlobalDefaultQoSPolicy oldDefault = globalDefaultPolicyRepository.findById(GLOBAL_DEFAULT_POLICY_ID)
					.orElse(new GlobalDefaultQoSPolicy());
			oldDefault.setDefaultPolicyId(policyId);
			newDefault = new QoSPolicyResponseDTO(policyRepository
					.findById(globalDefaultPolicyRepository.saveAndFlush(oldDefault).getDefaultPolicyId()).get());
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ArrowheadException(CoreCommonConstants.DATABASE_OPERATION_EXCEPTION_MSG);
		}

		return newDefault;
	}

	/**
	 * Returns the default policy set to the system with the specified identifier.
	 * 
	 * @param systemId Identifier of the system in question
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@Transactional(rollbackFor = ArrowheadException.class)
	public QoSPolicyResponseDTO getSystemDefaultPolicy(final long systemId) {
		List<SystemDefaultQoSPolicy> existingDefaultPolicy = systemDefaultPolicyRepository.findAll().stream()
				.filter(entry -> entry.getSystemId() == systemId).collect(Collectors.toList());

		QoSPolicyResponseDTO response = new QoSPolicyResponseDTO();
		if (existingDefaultPolicy.size() > 0) {
			Optional<QoSPolicy> foundPolicy = policyRepository
					.findById(existingDefaultPolicy.get(0).getDefaultPolicyId());
			if (foundPolicy.isPresent()) {
				response = new QoSPolicyResponseDTO(foundPolicy.get());
			} else {
				logger.warn("invalid policy id '{}' found for system with id '{}'",
						existingDefaultPolicy.get(0).getDefaultPolicyId(), systemId);
			}
		} else {
			logger.debug("no default policy found for system id '{}'", systemId);
		}

		return response;
	}

	/**
	 * Returns the default QoS policy for the specified system.
	 * 
	 * @param systemName Name of the system
	 * @param address    URL of the system
	 * @param port       Port of the system
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@Transactional(rollbackFor = ArrowheadException.class)
	public QoSPolicyResponseDTO getSystemDefaultPolicy(final String systemName, final String address, final int port) {
		logger.debug("getSystemDefaultPolicy started...");

		Optional<System> system = systemRepository.findBySystemNameAndAddressAndPort(systemName, address, port);
		QoSPolicyResponseDTO response = new QoSPolicyResponseDTO();
		if (system.isPresent()) {
			response = getSystemDefaultPolicy(system.get().getId());
		}

		return response;
	}

	/**
	 * Sets the default QoS policy for the requested system to the specified policy
	 * identifier.
	 * 
	 * @param systemId Identifier of the system
	 * @param policId  Identifier of the desired policy
	 * @return {@link QoSPolicyResponseDTO}
	 */
	@Transactional(rollbackFor = ArrowheadException.class)
	public QoSPolicyResponseDTO setSystemDefaultPolicy(final long systemId, final long policyId) {
		logger.debug("setSystemDefaultPolicy started...");

		if (!systemRepository.existsById(systemId)) {
			throw new InvalidParameterException("system with id '" + systemId + "' does not exist");
		}
		if (!policyRepository.existsById(policyId)) {
			throw new InvalidParameterException("policy with id '" + policyId + "' does not exist");
		}

		try {
			QoSPolicyResponseDTO newDefault = new QoSPolicyResponseDTO();
			List<SystemDefaultQoSPolicy> existingDefaultPolicy = systemDefaultPolicyRepository.findAll().stream()
					.filter(entry -> entry.getSystemId() == systemId).collect(Collectors.toList());

			SystemDefaultQoSPolicy oldDefault;
			if (existingDefaultPolicy.size() > 0) {
				// found a existing default policy for the requested system, update it
				oldDefault = existingDefaultPolicy.get(0);
				oldDefault.setDefaultPolicyId(policyId);
				;
			} else {
				// create new default policy
				oldDefault = new SystemDefaultQoSPolicy(systemId, policyId);
			}

			newDefault = new QoSPolicyResponseDTO(policyRepository
					.findById(systemDefaultPolicyRepository.saveAndFlush(oldDefault).getDefaultPolicyId()).get());

			return newDefault;
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ArrowheadException(CoreCommonConstants.DATABASE_OPERATION_EXCEPTION_MSG);
		}
	}
}

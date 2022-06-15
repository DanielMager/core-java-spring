package eu.arrowhead.common.database.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import eu.arrowhead.common.database.entity.QoSPolicy;

@Repository
public interface QoSPolicyRepository extends RefreshableRepository<QoSPolicy, Long> {

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	public Optional<QoSPolicy> findByName(String name);
}
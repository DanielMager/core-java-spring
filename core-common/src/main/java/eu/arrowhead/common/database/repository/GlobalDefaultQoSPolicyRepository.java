package eu.arrowhead.common.database.repository;

import org.springframework.stereotype.Repository;

import eu.arrowhead.common.database.entity.GlobalDefaultQoSPolicy;

@Repository
public interface GlobalDefaultQoSPolicyRepository extends RefreshableRepository<GlobalDefaultQoSPolicy, Long> {
}
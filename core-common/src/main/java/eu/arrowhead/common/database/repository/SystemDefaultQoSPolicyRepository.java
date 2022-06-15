package eu.arrowhead.common.database.repository;

import org.springframework.stereotype.Repository;

import eu.arrowhead.common.database.entity.SystemDefaultQoSPolicy;

@Repository
public interface SystemDefaultQoSPolicyRepository extends RefreshableRepository<SystemDefaultQoSPolicy, Long> {
}

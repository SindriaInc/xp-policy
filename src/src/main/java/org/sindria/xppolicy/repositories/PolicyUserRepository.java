package org.sindria.xppolicy.repositories;

import org.sindria.xppolicy.models.PolicyUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolicyUserRepository extends CrudRepository<PolicyUser, Long> {

    @Query("SELECT t FROM PolicyUser t WHERE t.user_id = :userId")
    List<PolicyUser> findByUserId(@Param("userId") String userId);

    @Query("SELECT t FROM PolicyUser t WHERE t.policy_id = :policyId")
    List<PolicyUser> findByPolicyId(@Param("policyId") Long policyId);
}

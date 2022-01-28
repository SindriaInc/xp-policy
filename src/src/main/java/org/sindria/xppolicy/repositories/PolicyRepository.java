package org.sindria.xppolicy.repositories;

import org.sindria.xppolicy.models.Policy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PolicyRepository extends CrudRepository<Policy, Long> {

    @Query("SELECT t FROM Policy t WHERE t.name LIKE CONCAT('%',:searchTerm, '%')")
    List<Policy> findBySearchTerm(@Param("searchTerm") String searchTerm);
}

package com.potagerai.domain.optimization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptimizationResultRepository extends JpaRepository<OptimizationResult, Long> {

    @Query("""
            SELECT r FROM OptimizationResult r
            LEFT JOIN FETCH r.plotAllocations pa
            LEFT JOIN FETCH pa.crop
            WHERE r.id = :id
            """)
    Optional<OptimizationResult> findByIdWithAllocations(Long id);

    @Query("""
            SELECT r FROM OptimizationResult r
            WHERE r.gardenProfile.id = :gardenProfileId
            ORDER BY r.computedAt DESC
            """)
    List<OptimizationResult> findByGardenProfileId(Long gardenProfileId);
}

package com.potagerai.domain.garden;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GardenProfileRepository extends JpaRepository<GardenProfile, Long> {

    @Query("SELECT g FROM GardenProfile g JOIN FETCH g.climateZone JOIN FETCH g.country WHERE g.user.id = :userId ORDER BY g.createdAt DESC")
    List<GardenProfile> findByUserId(Long userId);

    @Query("SELECT g FROM GardenProfile g JOIN FETCH g.climateZone JOIN FETCH g.country WHERE g.id = :id")
    Optional<GardenProfile> findByIdWithDetails(Long id);
}

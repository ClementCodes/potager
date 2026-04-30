package com.potagerai.domain.crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NutritionalProfileRepository extends JpaRepository<NutritionalProfile, Long> {

    Optional<NutritionalProfile> findByCropId(Long cropId);
}

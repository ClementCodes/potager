package com.potagerai.domain.climate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClimateZoneRepository extends JpaRepository<ClimateZone, String> {
}

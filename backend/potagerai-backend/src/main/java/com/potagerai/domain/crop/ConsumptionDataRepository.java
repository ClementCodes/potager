package com.potagerai.domain.crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsumptionDataRepository extends JpaRepository<ConsumptionData, Long> {

    List<ConsumptionData> findByCountryIsoCode(String countryIsoCode);

    Optional<ConsumptionData> findByCropIdAndCountryIsoCode(Long cropId, String countryIsoCode);
}

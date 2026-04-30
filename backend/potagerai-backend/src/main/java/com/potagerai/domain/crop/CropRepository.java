package com.potagerai.domain.crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    @Query("SELECT c FROM Crop c LEFT JOIN FETCH c.nutritionalProfile ORDER BY c.commonName")
    List<Crop> findAllWithNutritionalProfile();

    @Query("SELECT c FROM Crop c LEFT JOIN FETCH c.nutritionalProfile WHERE c.id = :id")
    Optional<Crop> findByIdWithNutritionalProfile(Long id);
}

package com.potagerai.domain.crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    @Query("SELECT c FROM Crop c LEFT JOIN FETCH c.nutritionalProfile ORDER BY c.commonName")
    List<Crop> findAllWithNutritionalProfile();

    @Query("SELECT c FROM Crop c LEFT JOIN FETCH c.nutritionalProfile WHERE c.id = :id")
    Optional<Crop> findByIdWithNutritionalProfile(Long id);

    /**
     * Filtre les cultures dont la fenêtre de semis chevauche la saison demandée.
     * <p>Les cultures sans données de semis ({@code sowingMonthMin IS NULL}) sont toujours incluses.
     * Pour la saison hiver (monthStart > monthEnd), la condition est inversée.
     *
     * @param monthStart premier mois de la saison (inclus)
     * @param monthEnd   dernier mois de la saison (inclus)
     * @param wrapsAround {@code true} pour l'hiver (déc-jan-fév)
     */
    @Query("""
            SELECT c FROM Crop c LEFT JOIN FETCH c.nutritionalProfile
            WHERE c.sowingMonthMin IS NULL
               OR (:wrapsAround = false
                    AND c.sowingMonthMin <= :monthEnd
                    AND c.sowingMonthMax >= :monthStart)
               OR (:wrapsAround = true
                    AND (c.sowingMonthMin >= :monthStart OR c.sowingMonthMax <= :monthEnd))
            ORDER BY c.commonName
            """)
    List<Crop> findAvailableInSeason(
            @Param("monthStart")  int monthStart,
            @Param("monthEnd")    int monthEnd,
            @Param("wrapsAround") boolean wrapsAround);
}

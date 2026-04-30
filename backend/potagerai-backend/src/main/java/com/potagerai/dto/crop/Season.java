package com.potagerai.dto.crop;

/**
 * Saisons de semis utilisées pour filtrer les cultures disponibles.
 *
 * <p>Les bornes correspondent aux mois de semis (champ {@code sowingMonthMin/Max} de {@code Crop}).
 * L'hiver gère le chevauchement de fin d'année (décembre-février).
 */
public enum Season {

    /** Mars – Mai */
    PRINTEMPS(3, 5, false),

    /** Juin – Août */
    ETE(6, 8, false),

    /** Septembre – Novembre */
    AUTOMNE(9, 11, false),

    /** Décembre – Février (chevauchement de fin d'année) */
    HIVER(12, 2, true),

    /** Pas de filtre — toutes les cultures */
    TOUTE_ANNEE(1, 12, false);

    public final int monthStart;
    public final int monthEnd;
    /**
     * {@code true} si la fenêtre de semis chevauche la fin d'année
     * (ex. décembre → janvier → février).
     */
    public final boolean wrapsAround;

    Season(int monthStart, int monthEnd, boolean wrapsAround) {
        this.monthStart   = monthStart;
        this.monthEnd     = monthEnd;
        this.wrapsAround  = wrapsAround;
    }
}

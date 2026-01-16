package com.sentiment.backend.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO de respuesta para estadísticas del dashboard.
 *
 * Mantiene nombres de campos alineados con el frontend actual.
 */
@Getter
@AllArgsConstructor
public class DashboardStatsDTO {
    @JsonProperty("total_analizados")
    // Total de análisis registrados
    private final long totalAnalizados;

    @JsonProperty("positivos")
    // Cantidad de positivos
    private final long positivos;

    @JsonProperty("negativos")
    // Cantidad de negativos
    private final long negativos;

    @JsonProperty("porcentaje_positivos")
    // Porcentaje formateado de positivos (ej: "65%")
    private final String porcentajePositivos;

    @JsonProperty("porcentaje_negativos")
    // Porcentaje formateado de negativos (ej: "35%")
    private final String porcentajeNegativos;

    @JsonProperty("top_words_pos")
    // Top palabras asociadas a positivos
    private final Map<String, Integer> topWordsPos;

    @JsonProperty("top_words_neg")
    // Top palabras asociadas a negativos
    private final Map<String, Integer> topWordsNeg;
}

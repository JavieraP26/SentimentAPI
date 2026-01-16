package com.sentiment.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de respuesta para el endpoint de analytics.
 *
 * Resume totales y promedio de confianza del sistema.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponseDTO {
    // Total de análisis registrados en el sistema
    private long totalAnalisis;
    // Cantidad de análisis positivos
    private long positivos;
    // Cantidad de análisis negativos
    private long negativos;
    // Promedio de confianza del modelo (0-1)
    private double promedioConfianza;

}
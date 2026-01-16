package com.sentiment.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para análisis batch.
 *
 * Devuelve el total de resultados y la lista ordenada de predicciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentBatchResponseDTO {
    // Total de resultados devueltos (puede ser menor si DS devuelve menos items)
    private int total;
    // Resultados en el mismo orden del input
    private List<SentimentResponseDTO> results;
}

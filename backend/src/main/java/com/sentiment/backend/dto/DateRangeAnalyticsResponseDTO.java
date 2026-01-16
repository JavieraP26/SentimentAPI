package com.sentiment.backend.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de salida para consultas por rango de fechas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateRangeAnalyticsResponseDTO {

    // Fecha inicial (dd-MM-yyyy)
    private String desde;
    // Fecha final (dd-MM-yyyy)
    private String hasta;
    // Total de registros en el rango
    private long total;
    // Conteo de positivos
    private long positivos;
    // Conteo de negativos
    private long negativos;
    // Porcentaje de positivos (ej: "65%")
    private String porcentajePositivos;
    // Porcentaje de negativos (ej: "35%")
    private String porcentajeNegativos;
    // Top palabras positivas
    private Map<String, Integer> topWordsPos;
    // Top palabras negativas
    private Map<String, Integer> topWordsNeg;
    // Lista de registros del rango
    private List<SentimentRecordDTO> registros;
    // Página actual (0-based)
    private int page;
    // Tamaño de página
    private int size;
    // Total de páginas
    private int totalPages;
}

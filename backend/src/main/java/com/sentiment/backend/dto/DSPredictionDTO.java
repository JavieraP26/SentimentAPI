package com.sentiment.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir la respuesta del microservicio de Data Science.
 *
 * Este DTO es interno (no se expone al cliente final).
 * Se usa únicamente para deserializar la respuesta JSON del endpoint /predict de DS.
 *
 * Ejemplo de respuesta de DS:
 * {
 *   "sentiment": "Positive",
 *   "probability": 0.92
 * }
 *
 * Los campos están en inglés porque DS trabaja con estándar ML convencional.
 * El Service traduce "Positive" → "Positivo" antes de responder al cliente.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DSPredictionDTO {

    /**
     * Clasificación del sentimiento en inglés.
     * Valores esperados: "Positive", "Negative", "Neutral"
     */
    private String sentiment;

    /**
     * Probabilidad del resultado (0.0 - 1.0).
     * Ejemplo: 0.92 = 92% de confianza
     */
    private Double probability;
    
}

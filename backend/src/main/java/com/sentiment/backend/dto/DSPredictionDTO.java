package com.sentiment.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * DTO para recibir la respuesta del microservicio de Data Science.
 *
 * Este DTO es interno (no se expone al cliente final).
 * Se usa únicamente para deserializar la respuesta JSON del endpoint /predict de DS.
 *
 * Ejemplo de respuesta de DS:
 * {
 *   "sentiment": "Positive",
 *   "probability": 0.92,
 *   "translatedText": "The service is excellent and very helpful",
 *   "topFeatures": ["excellent", "helpful", "service"]
 * }
 *
 * Los campos están en inglés porque DS trabaja con estándar ML convencional.
 * El Service traduce los valores antes de responder al cliente final.
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DSPredictionDTO {

    /**
     * Clasificación del sentimiento en inglés.
     *
     * Valores esperados según modelo binario:
     * - "Positive" - Sentimiento positivo
     * - "Negative" - Sentimiento negativo
     *
     * Este valor es traducido a español por el Service antes de retornarlo al cliente.
     */
    private String sentiment;

    /**
     * Probabilidad del resultado (0.0 - 1.0).
     *
     * Representa la confianza del modelo ML en la predicción.
     * Ejemplo: 0.92 = 92% de confianza en que el sentimiento es correcto.
     *
     * Rango válido: [0.0, 1.0]
     * - Valores cercanos a 1.0 indican alta confianza
     * - Valores cercanos a 0.5 indican incertidumbre
     */
    private Double probability;

    /**
     * Texto traducido al inglés por el microservicio de DS.
     *
     * Si el texto original estaba en otro idioma, este campo contiene
     * la traducción al inglés que fue usada para el análisis ML.
     * Si el texto original ya estaba en inglés, este campo contiene el mismo texto.
     *
     * Ejemplo: "El servicio es excelente" → "The service is excellent"
     *
     * Este campo es opcional en la respuesta al cliente final, dependiendo
     * de los requisitos del frontend/dashboard.
     */

    private String translatedText;

    /**
     * Lista de palabras más influyentes en la predicción (explicabilidad).
     *
     * Contiene las palabras o tokens que el modelo ML identificó como
     * más relevantes para determinar el sentimiento.
     *
     * Ejemplo: ["excellent", "helpful", "service"]
     *
     * Esta información permite al usuario entender por qué el modelo
     * clasificó el texto de cierta manera (explicabilidad básica del modelo).
     *
     * Nota: Si el modelo no retorna este campo, se registra un warning
     * pero no se considera error crítico.
     */

    private List<String> topFeatures;
    
}

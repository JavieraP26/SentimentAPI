package com.sentiment.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO de respuesta para el endpoint POST /v1/sentiment.
 *
 * Representa la respuesta al cliente con el resultado del análisis de sentimiento.
 * Todos los valores están en español según el contrato de la API.
 *
 * Ejemplo de respuesta:
 * {
 *   "prevision": "Positivo",
 *   "probabilidad": 0.92,
 *   "textoTraducido": "The service is excellent",
 *   "palabrasClave": ["excellent", "service"]
 * }
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentResponseDTO {

    /**
     * Clasificación del sentimiento en español.
     *
     * Valores posibles:
     * - "Positivo" - Sentimiento positivo detectado
     * - "Negativo" - Sentimiento negativo detectado
     *
     * Este valor es la traducción del campo "sentiment" de DS.
     */
    private String prevision;

    /**
     * Probabilidad del resultado (0.0 - 1.0).
     *
     * Representa el nivel de confianza del modelo en la predicción.
     *
     * Ejemplos:
     * - 0.92 → 92% de confianza (alta certeza)
     * - 0.65 → 65% de confianza (confianza moderada)
     * - 0.51 → 51% de confianza (baja certeza)
     *
     * Valores más cercanos a 1.0 indican mayor seguridad en el resultado.
     */
    private Double probabilidad;

    /**
     * Texto traducido al inglés (idioma usado para el análisis ML).
     *
     * Si el texto original estaba en español u otro idioma, este campo
     * muestra la traducción que fue analizada por el modelo.
     *
     * Campo opcional que puede ser usado por el dashboard para:
     * - Mostrar la traducción al usuario
     * - Verificar la calidad de la traducción automática
     * - Debugging de resultados inesperados
     *
     * Puede ser null si el frontend no lo requiere.
     */
    private String textoTraducido;

    /**
     * Palabras clave que influyeron en la predicción (explicabilidad).
     *
     * Lista de palabras en inglés que el modelo ML identificó como
     * más relevantes para determinar el sentimiento.
     *
     * Ejemplo: ["excellent", "helpful", "service"]
     *
     * Este campo permite al usuario entender qué aspectos del texto
     * fueron más importantes en la clasificación, mejorando la
     * transparencia y confianza en el sistema.
     *
     * Uso en dashboard:
     * - Mostrar palabras destacadas visualmente
     * - Generar "nubes de palabras" en analytics
     * - Identificar patrones en feedback negativo/positivo
     */
    private List<String> palabrasClave;
}

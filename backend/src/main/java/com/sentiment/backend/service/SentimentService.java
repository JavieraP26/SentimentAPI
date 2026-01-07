package com.sentiment.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sentiment.backend.client.DSClient;
import com.sentiment.backend.domain.Sentiment;
import com.sentiment.backend.dto.DSPredictionDTO;
import com.sentiment.backend.dto.SentimentRequestDTO;
import com.sentiment.backend.dto.SentimentResponseDTO;
import com.sentiment.backend.repository.SentimentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio de lógica de negocio para el análisis de sentimiento.
 *
 * Responsabilidades:
 * 1. Coordinar la llamada al microservicio de Data Science
 * 2. Traducir respuestas técnicas (inglés) a formato de negocio (español)
 * 3. Persistir resultados en PostgreSQL para analytics
 * 4. Retornar respuesta al Controller según contrato de NoCountry
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SentimentService {

    private final DSClient dsClient;
    private final SentimentRepository sentimentRepository;

    /**
     * Analiza el sentimiento de un texto en inglés.
     *
     * Flujo:
     * 1. Llamar a DS para obtener predicción ML
     * 2. Validar que la respuesta de DS sea completa
     * 3. Traducir sentimiento (inglés → español)
     * 4. Guardar en BD para historial
     * 5. Retornar respuesta traducida
     *
     * @param request DTO con el texto validado
     * @return DTO con predicción en español y probabilidad
     * @throws IllegalStateException si DS retorna respuesta incompleta
     */
    @Transactional
    public SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request) {
        String text = request.getText();

        log.info("Analizando sentimiento para texto: {}",
                text.substring(0, Math.min(50, text.length())) + "...");

        // Llamar a Data Science
        DSPredictionDTO prediction = dsClient.predict(text);

        // Validar respuesta de DS (rechazar si está incompleta)
        validarRespuestaDS(prediction);

        log.info("Respuesta de DS - sentiment: {}, probability: {}",
                prediction.getSentiment(),
                prediction.getProbability());

        // Traducir sentimiento a español
        String previsionEspanol = traducirSentimiento(prediction.getSentiment());

        // Construir entidad para persistir (ahora es seguro, ya validamos)
        Sentiment sentiment = Sentiment.builder()
                .text(text)
                .prevision(previsionEspanol)
                .probabilidad(prediction.getProbability())
                .build();

        sentimentRepository.save(sentiment);

        log.info("Sentimiento guardado en BD con ID: {}", sentiment.getId());

        // Construir y retornar respuesta
        return new SentimentResponseDTO(
                previsionEspanol,
                prediction.getProbability()
        );
    }

    /**
     * Valida que la respuesta de DS contenga todos los campos requeridos.
     *
     * @param prediction Respuesta de DS
     * @throws IllegalStateException si la respuesta es null o tiene campos null
     */
    private void validarRespuestaDS(DSPredictionDTO prediction) {
        if (prediction == null) {
            log.error("DS retornó respuesta null");
            throw new IllegalStateException("Modelo Data Science retornó respuesta vacía");
        }

        if (prediction.getSentiment() == null || prediction.getSentiment().isBlank()) {
            log.error("DS retornó sentiment null o vacío: {}", prediction);
            throw new IllegalStateException("Modelo Data Science retornó sentiment inválido");
        }

        if (prediction.getProbability() == null) {
            log.error("DS retornó probability null: {}", prediction);
            throw new IllegalStateException("Modelo Data Science retornó probability inválida");
        }

        // Validar rango de probabilidad (0.0 - 1.0)
        if (prediction.getProbability() < 0.0 || prediction.getProbability() > 1.0) {
            log.error("DS retornó probability fuera de rango [0.0, 1.0]: {}", prediction.getProbability());
            throw new IllegalStateException("Modelo Data Science retornó probability fuera de rango: " + prediction.getProbability());
        }
    }

    /**
     * Traduce el sentimiento de inglés (DS) a español (cliente).
     *
     * Mapeo:
     * - Positive → Positivo
     * - Negative → Negativo
     * - Neutral → Neutral
     *
     * @param sentimentEnIngles Sentimiento en inglés de DS
     * @return Sentimiento traducido al español
     */
    private String traducirSentimiento(String sentimentEnIngles) {
        return switch (sentimentEnIngles) {
            case "Positive" -> "Positivo";
            case "Negative" -> "Negativo";
            case "Neutral" -> "Neutral";
            default -> {
                log.warn("Sentimiento desconocido recibido de DS: {}", sentimentEnIngles);
                yield "Desconocido";
            }
        };
    }
}

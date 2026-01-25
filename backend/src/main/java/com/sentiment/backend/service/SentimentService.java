package com.sentiment.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sentiment.backend.client.DSClient;
import com.sentiment.backend.domain.Sentiment;
import com.sentiment.backend.dto.DSPredictionDTO;
import com.sentiment.backend.dto.SentimentBatchResponseDTO;
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
 * 2. Validar respuestas del modelo ML (incluyendo traducción y features)
 * 3. Traducir clasificación de sentimiento (inglés → español)
 * 4. Persistir resultados completos en PostgreSQL para analytics e historial
 * 5. Retornar respuesta formateada al Controller según contrato de la API
 *
 * Este servicio actúa como orquestador entre el cliente HTTP (DSClient),
 * la capa de persistencia (Repository) y el Controller.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SentimentService {

    private final DSClient dsClient;
    private final SentimentRepository sentimentRepository;

    /**
     * Analiza el sentimiento de un texto en cualquier idioma.
     *
     * El microservicio de Data Science se encarga automáticamente de:
     * - Detectar el idioma del texto
     * - Traducir al inglés si es necesario
     * - Ejecutar el modelo ML de análisis de sentimiento
     * - Retornar traducción y palabras clave influyentes
     *
     * Flujo completo:
     * 1. Llamar a DS para obtener predicción ML con traducción y features
     * 2. Validar que la respuesta de DS sea completa y válida
     * 3. Traducir clasificación de sentimiento (inglés → español)
     * 4. Construir entidad con todos los campos (original + procesados)
     * 5. Persistir en BD para historial y analytics
     * 6. Retornar respuesta formateada al cliente
     *
     * @param request DTO con el texto validado (10-500 caracteres, no vacío)
     * @return DTO con predicción en español, probabilidad, traducción y palabras clave
     * @throws IllegalStateException si DS retorna respuesta incompleta o inválida
     */
    @Transactional
    public SentimentResponseDTO analyzeSentiment(SentimentRequestDTO request) {
        String text = request.getText();

        // Log defensivo: mostramos solo el inicio del texto
        log.info("Analizando sentimiento para texto: {}",
                text.substring(0, Math.min(50, text.length())) + "...");

        // Llamar a Data Science (DS traduce, analiza y retorna features)
        DSPredictionDTO prediction = dsClient.predict(text);
        // Persistimos el resultado y devolvemos DTO de respuesta
        return persistAndMap(text, prediction);
    }

    /**
     * Analiza un lote de textos usando el mismo flujo del análisis individual.
     *
     * @param texts Lista de textos ya validados (10-500 caracteres, no vacíos)
     * @return Respuesta con total y resultados por texto
     */
    @Transactional
    public SentimentBatchResponseDTO analyzeBatch(List<String> texts) {
        // Se delega el análisis masivo a DS y se procesa el resultado
        List<DSPredictionDTO> predictions = dsClient.predictBatch(texts);

        if (predictions.size() != texts.size()) {
            log.error("DS batch devolvió {} predicciones para {} textos", predictions.size(), texts.size());
            throw new IllegalStateException("Modelo Data Science devolvió un tamaño de lote inconsistente");
        }

        int limit = predictions.size();
        List<SentimentResponseDTO> results = new java.util.ArrayList<>(limit);

        // Persistimos y mapeamos por índice para mantener el orden del input
        for (int i = 0; i < limit; i++) {
            results.add(persistAndMap(texts.get(i), predictions.get(i)));
        }

        SentimentBatchResponseDTO response = new SentimentBatchResponseDTO();
        response.setTotal(results.size());
        response.setResults(results);
        return response;
    }

    private SentimentResponseDTO persistAndMap(String text, DSPredictionDTO prediction) {
        // Validación defensiva de la respuesta de DS
        validarRespuestaDS(prediction);

        log.info("Respuesta de DS - sentiment: {}, probability: {}, features: {}",
                prediction.getSentiment(),
                prediction.getProbability(),
                prediction.getTopFeatures() != null ? prediction.getTopFeatures().size() : 0);

        // Traducir sentimiento a español para respuesta al cliente
        String previsionEspanol = traducirSentimiento(prediction.getSentiment());

        String palabrasClave = prediction.getTopFeatures() == null
                ? null
                : String.join(", ", prediction.getTopFeatures());
        if (palabrasClave != null && palabrasClave.length() > 500) {
            log.warn("palabrasClave supera 500 caracteres ({}). Se truncará en un delimitador para persistencia.", palabrasClave.length());
            palabrasClave = truncateByDelimiter(palabrasClave, 500, ", ");
        }

        // Construir entidad con todos los campos para persistencia
        Sentiment sentiment = Sentiment.builder()
                .text(text)
                .prevision(previsionEspanol)
                .probabilidad(prediction.getProbability())
                .textoTraducido(prediction.getTranslatedText())
                .palabrasClave(palabrasClave)
                .build();

        // Guardar el análisis para historial y analytics
        sentimentRepository.save(sentiment);

        log.info("Sentimiento guardado en BD con ID: {}", sentiment.getId());

        // Construir y retornar respuesta al cliente
        return new SentimentResponseDTO(
                previsionEspanol,
                prediction.getProbability(),
                prediction.getTranslatedText(),  // Opcional, el frontend decide si mostrarlo
                prediction.getTopFeatures()  // Para explicabilidad/dashboard
        );
    }

    /**
     * Valida que la respuesta de DS contenga todos los campos requeridos y en rangos válidos.
     *
     * Validaciones aplicadas:
     * - Respuesta no null
     * - Sentiment no null ni vacío
     * - Probability no null y en rango [0.0, 1.0]
     * - TranslatedText opcional (si viene null, se permite)
     * - TopFeatures no vacío (warning si falta, no crítico)
     *
     * @param prediction Respuesta de DS a validar
     * @throws IllegalStateException si alguna validación crítica falla
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

        // TranslatedText es opcional: si viene vacío, solo se registra warning
        if (prediction.getTranslatedText() == null || prediction.getTranslatedText().isBlank()) {
            log.warn("DS no retornó translatedText. Se continuará sin traducción.");
        }

        // Validar top features (warning solamente, no crítico)
        // NOTA: Confirmar con DS si la ausencia de features debe ser error crítico
        if (prediction.getTopFeatures() == null || prediction.getTopFeatures().isEmpty()) {
            log.warn("DS no retornó top features. Esto puede afectar la explicabilidad del modelo.");
            // No lanza excepción porque no es crítico para el funcionamiento básico
            // pero se registra para investigación futura
        }
    }

    /**
     * Traduce el sentimiento de inglés (formato DS) a español (formato cliente).
     *
     * Mapeo definido según modelo binario:
     * - "Positive" → "Positivo"
     * - "Negative" → "Negativo"
     *
     * Si DS retorna un valor no esperado, se registra warning y se retorna "Desconocido"
     * para evitar que el sistema falle completamente.
     *
     * @param sentimentEnIngles Sentimiento en inglés retornado por DS
     * @return Sentimiento traducido al español
     */
    private String traducirSentimiento(String sentimentEnIngles) {
        return switch (sentimentEnIngles) {
            case "Positive" -> "Positivo";
            case "Negative" -> "Negativo";
            default -> {
                log.warn("Sentimiento desconocido recibido de DS: {}. " +
                        "Se esperaba 'Positive' o 'Negative'.", sentimentEnIngles);
                yield "Desconocido";
            }
        };
    }

    private String truncateByDelimiter(String value, int maxLength, String delimiter) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        int limit = Math.min(maxLength, value.length());
        int lastDelimiter = value.lastIndexOf(delimiter, limit);
        if (lastDelimiter <= 0) {
            return value.substring(0, maxLength);
        }
        return value.substring(0, lastDelimiter);
    }
}

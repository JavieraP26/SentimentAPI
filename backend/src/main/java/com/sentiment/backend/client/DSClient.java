package com.sentiment.backend.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.sentiment.backend.dto.DSPredictionDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * Cliente HTTP para comunicarse con el microservicio de Data Science.
 *
 * Encapsula toda la lógica de comunicación HTTP con DS:
 * - Construcción de requests JSON
 * - Configuración de timeouts
 * - Deserialización de respuestas
 *
 * URL configurable vía application.properties:
 * ds.service.url=http://localhost:8001
 */
@Slf4j
@Component
public class DSClient {

    @Value("${ds.service.url:http://localhost:8001}")
    private String dsServiceUrl;

    private final RestTemplate restTemplate;

    /**
     * Constructor que configura el RestTemplate con timeouts.
     */
    public DSClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);  // 5 segundos para establecer conexión
        factory.setReadTimeout(10000);    // 10 segundos para leer respuesta

        this.restTemplate = new RestTemplate(factory);
    }

    /**
     * Llama al endpoint /predict de Data Science para analizar sentimiento.
     *
     * @param text Texto en inglés a analizar (validado previamente)
     * @return DSPredictionDTO con sentimiento y probabilidad
     * @throws RestClientException si DS no responde o retorna error HTTP
     */
    public DSPredictionDTO predict(String text) {
        // Preparar body JSON: {"text": "..."}
        Map<String, String> body = Map.of("text", text);

        // Preparar headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Construir request HTTP
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        // URL completa del endpoint
        String url = dsServiceUrl + "/predict";

        log.info("Llamando a DS: {}", url);
        log.debug("Request body: {}", body);

        try {
            // Hacer llamada HTTP POST
            ResponseEntity<DSPredictionDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    DSPredictionDTO.class
            );

            DSPredictionDTO prediction = response.getBody();

            // Validación defensiva
            if (prediction == null) {
                log.error("DS retornó body vacío");
                throw new RestClientException("DS response body is null");
            }

            log.info("Respuesta de DS - sentiment: {}, probability: {}",
                    prediction.getSentiment(),
                    prediction.getProbability());

            return prediction;

        } catch (RestClientException e) {
            log.error("Error al llamar a DS: {}", e.getMessage());
            throw e; // Re-lanzar para que GlobalExceptionHandler lo maneje
        }
    }
}

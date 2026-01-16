package com.sentiment.backend.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sentiment.backend.dto.SentimentBatchResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para procesar archivos CSV enviados desde el dashboard.
 *
 * Extrae textos, arma un batch y delega el análisis al servicio principal.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CsvBatchService {

    private static final int MIN_TEXT_LENGTH = 20;
    private static final int MAX_TEXT_LENGTH = 500;

    private final SentimentService sentimentService;

    public int processCsv(MultipartFile file) {
        // Recolectamos los textos del CSV para enviarlos en un solo batch
        List<String> texts = new ArrayList<>();

        // Lectura línea por línea para evitar cargar todo el archivo en memoria
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                String raw = line.trim();
                if (raw.isEmpty()) {
                    continue;
                }

                // Saltar encabezado si se detecta la columna de texto
                if (firstLine && looksLikeHeader(raw)) {
                    firstLine = false;
                    continue;
                }
                firstLine = false;

                // Extraer primera columna como texto a analizar
                String text = extractFirstColumn(raw);
                if (!isValidText(text)) {
                    continue;
                }

                // Acumulamos y luego procesamos en batch
                texts.add(text);
            }
        } catch (IOException ex) {
            log.error("Error leyendo CSV: {}", ex.getMessage(), ex);
            return 0;
        }

        // Si no hay textos válidos, retornamos 0
        if (texts.isEmpty()) {
            return 0;
        }

        try {
            // El batch llama a DS y persiste cada resultado
            SentimentBatchResponseDTO response = sentimentService.analyzeBatch(texts);
            return response.getTotal();
        } catch (Exception ex) {
            log.error("Error procesando batch CSV: {}", ex.getMessage());
            return 0;
        }
    }

    private boolean looksLikeHeader(String line) {
        String lower = line.toLowerCase();
        return lower.contains("text") || lower.contains("feedback") || lower.contains("coment");
    }

    private String extractFirstColumn(String line) {
        // Extraemos la primera columna, soportando CSV simple con comas
        String first = line;
        int commaIndex = line.indexOf(',');
        if (commaIndex >= 0) {
            first = line.substring(0, commaIndex);
        }

        String trimmed = first.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            trimmed = trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

    private boolean isValidText(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        int length = text.length();
        return length >= MIN_TEXT_LENGTH && length <= MAX_TEXT_LENGTH;
    }
}

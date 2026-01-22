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

    private static final int MIN_TEXT_LENGTH = 10;
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
        return lower.contains("text")
                || lower.contains("texto")
                || lower.contains("feedback")
                || lower.contains("comentario")
                || lower.contains("comentarios")
                || lower.contains("opinion")
                || lower.contains("opiniones")
                || lower.contains("mensaje")
                || lower.contains("mensajes");
    }

    private String extractFirstColumn(String line) {
        String firstField = parseFirstCsvField(line);
        return normalizeCsvText(firstField);
    }

    private boolean isValidText(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        int length = text.length();
        return length >= MIN_TEXT_LENGTH && length <= MAX_TEXT_LENGTH;
    }

    private String parseFirstCsvField(String line) {
        StringBuilder result = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    result.append('"');
                    i++;
                    continue;
                }
                inQuotes = !inQuotes;
                continue;
            }
            if (!inQuotes && current == ',') {
                break;
            }
            result.append(current);
        }

        return result.toString().trim();
    }

    private String normalizeCsvText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }
        String unescaped = trimmed.replace("\"\"", "\"");
        if (unescaped.startsWith("\"") && unescaped.endsWith("\"") && unescaped.length() >= 2) {
            return unescaped.substring(1, unescaped.length() - 1).trim();
        }
        return unescaped.trim();
    }
}

package com.sentiment.backend.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sentiment.backend.domain.Sentiment;
import com.sentiment.backend.dto.AnalyticsResponseDTO;
import com.sentiment.backend.dto.DateRangeAnalyticsResponseDTO;
import com.sentiment.backend.dto.DashboardStatsDTO;
import com.sentiment.backend.dto.SentimentRecordDTO;
import com.sentiment.backend.repository.SentimentRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para estadísticas y métricas agregadas.
 *
 * Calcula totales, porcentajes y top words a partir de los registros persistidos.
 */
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final String POSITIVO = "Positivo";
    private static final String NEGATIVO = "Negativo";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final SentimentRepository sentimentRepository;

    public AnalyticsResponseDTO getAnalytics() {
        // Totales básicos para analytics general
        long total = sentimentRepository.count();
        long positivos = sentimentRepository.countByPrevision(POSITIVO);
        long negativos = sentimentRepository.countByPrevision(NEGATIVO);
        Double promedio = sentimentRepository.getAverageProbabilidad();

        // Promedio puede ser null si no hay registros
        return new AnalyticsResponseDTO(
                total,
                positivos,
                negativos,
                promedio != null ? promedio : 0.0
        );
    }

    public DashboardStatsDTO getDashboardStats() {
        // Métricas para el dashboard (totales + porcentajes + top words)
        long total = sentimentRepository.count();
        long positivos = sentimentRepository.countByPrevision(POSITIVO);
        long negativos = sentimentRepository.countByPrevision(NEGATIVO);

        String porcentajePositivos = formatPercentage(positivos, total);
        String porcentajeNegativos = formatPercentage(negativos, total);

        Map<String, Integer> topWordsPos = new HashMap<>();
        Map<String, Integer> topWordsNeg = new HashMap<>();

        // Recorremos registros y acumulamos palabras clave por sentimiento
        for (Sentiment sentiment : sentimentRepository.findAll()) {
            String palabras = sentiment.getPalabrasClave();
            if (palabras == null || palabras.isBlank()) {
                continue;
            }

            Map<String, Integer> target =
                    POSITIVO.equalsIgnoreCase(sentiment.getPrevision()) ? topWordsPos : topWordsNeg;

            for (String raw : palabras.split(",")) {
                String palabra = raw.trim();
                if (palabra.isEmpty()) {
                    continue;
                }
                target.put(palabra, target.getOrDefault(palabra, 0) + 1);
            }
        }

        // Top words ordenadas por frecuencia (limitadas)
        return new DashboardStatsDTO(
                total,
                positivos,
                negativos,
                porcentajePositivos,
                porcentajeNegativos,
                topWordsPosDesc(topWordsPos, 8),
                topWordsPosDesc(topWordsNeg, 8)
        );
    }

    public DateRangeAnalyticsResponseDTO getAnalyticsByDateRange(
            LocalDate desde,
            LocalDate hasta,
            String sentiment,
            int page,
            int size) {
        // Normalizamos el rango para cubrir el día completo
        LocalDateTime start = desde.atStartOfDay();
        LocalDateTime end = hasta.atTime(LocalTime.MAX);

        Pageable pageable = PageRequest.of(page, size);
        Page<Sentiment> pageResult;
        List<Sentiment> records;
        long total;

        if (sentiment == null || sentiment.isBlank()) {
            pageResult = sentimentRepository.findByCreatedAtBetween(start, end, pageable);
            records = sentimentRepository.findByCreatedAtBetween(start, end);
            total = sentimentRepository.countByCreatedAtBetween(start, end);
        } else {
            pageResult = sentimentRepository.findByRangeAndSentiment(start, end, sentiment, pageable);
            records = sentimentRepository.findAllByRangeAndSentiment(start, end, sentiment);
            total = sentimentRepository.countByRangeAndSentiment(start, end, sentiment);
        }
        long positivos = records.stream().filter(r -> POSITIVO.equalsIgnoreCase(r.getPrevision())).count();
        long negativos = records.stream().filter(r -> NEGATIVO.equalsIgnoreCase(r.getPrevision())).count();

        String porcentajePositivos = formatPercentage(positivos, total);
        String porcentajeNegativos = formatPercentage(negativos, total);

        Map<String, Integer> topWordsPos = new HashMap<>();
        Map<String, Integer> topWordsNeg = new HashMap<>();

        // Acumula palabras clave solo dentro del rango consultado
        for (Sentiment record : records) {
            String palabras = record.getPalabrasClave();
            if (palabras == null || palabras.isBlank()) {
                continue;
            }

            Map<String, Integer> target =
                    POSITIVO.equalsIgnoreCase(record.getPrevision()) ? topWordsPos : topWordsNeg;

            for (String raw : palabras.split(",")) {
                String palabra = raw.trim();
                if (palabra.isEmpty()) {
                    continue;
                }
                target.put(palabra, target.getOrDefault(palabra, 0) + 1);
            }
        }

        List<SentimentRecordDTO> registros = pageResult.getContent().stream()
                .map(this::mapRecord)
                .toList();

        return new DateRangeAnalyticsResponseDTO(
                DATE_FORMAT.format(desde),
                DATE_FORMAT.format(hasta),
                total,
                positivos,
                negativos,
                porcentajePositivos,
                porcentajeNegativos,
                topWordsPosDesc(topWordsPos, 8),
                topWordsPosDesc(topWordsNeg, 8),
                registros,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalPages()
        );
    }

    public byte[] exportAnalyticsByDateRange(LocalDate desde, LocalDate hasta, String sentiment) {
        LocalDateTime start = desde.atStartOfDay();
        LocalDateTime end = hasta.atTime(LocalTime.MAX);

        List<Sentiment> records = sentimentRepository.findAllByRangeAndSentiment(start, end, sentiment);

        StringBuilder csv = new StringBuilder();
        csv.append("\uFEFF");
        csv.append("sep=;\n");
        csv.append("Fecha;Texto Original;Texto Interpretado;Prevision;Probabilidad;Palabras clave\n");

        for (Sentiment sentimentRow : records) {
            csv.append(escapeCsv(DATE_FORMAT.format(sentimentRow.getCreatedAt()))).append(';')
               .append(escapeCsv(sentimentRow.getText())).append(';')
               .append(escapeCsv(sentimentRow.getTextoTraducido())).append(';')
               .append(escapeCsv(sentimentRow.getPrevision())).append(';')
               .append(escapeCsv(String.valueOf(sentimentRow.getProbabilidad()))).append(';')
               .append(escapeCsv(sentimentRow.getPalabrasClave()))
               .append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String formatPercentage(long part, long total) {
        // Evitar división por cero
        if (total == 0) {
            return "0%";
        }
        double value = (part * 100.0) / total;
        return String.format(Locale.US, "%.0f%%", value);
    }

    private Map<String, Integer> topWordsPosDesc(Map<String, Integer> input, int limit) {
        // Ordenar por frecuencia descendente y recortar a N elementos
        return input.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private SentimentRecordDTO mapRecord(Sentiment sentiment) {
        List<String> palabras = List.of();
        if (sentiment.getPalabrasClave() != null && !sentiment.getPalabrasClave().isBlank()) {
            palabras = List.of(sentiment.getPalabrasClave().split(","))
                    .stream()
                    .map(String::trim)
                    .filter(value -> !value.isEmpty())
                    .toList();
        }

        return new SentimentRecordDTO(
                DATE_FORMAT.format(sentiment.getCreatedAt()),
                sentiment.getText(),
                sentiment.getTextoTraducido(),
                sentiment.getPrevision(),
                sentiment.getProbabilidad(),
                palabras
        );
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}

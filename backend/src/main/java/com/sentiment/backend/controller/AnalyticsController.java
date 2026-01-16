package com.sentiment.backend.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sentiment.backend.dto.AnalyticsResponseDTO;
import com.sentiment.backend.dto.DateRangeAnalyticsResponseDTO;
import com.sentiment.backend.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

/**
 * Controller REST para analytics.
 *
 * Expone un endpoint con métricas agregadas del sistema.
 */
@RestController
@RequestMapping("/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsResponseDTO> getAnalytics() {
        // Devuelve totales y promedio de confianza
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }

    @GetMapping("/range")
    public ResponseEntity<DateRangeAnalyticsResponseDTO> getAnalyticsByRange(
            @RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") java.time.LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") java.time.LocalDate endDate,
            @RequestParam(value = "sentiment", required = false) String sentiment,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size) {
        // Devuelve registros y métricas solo del rango solicitado
        String normalized = normalizeSentiment(sentiment);
        return ResponseEntity.ok(analyticsService.getAnalyticsByDateRange(startDate, endDate, normalized, page, size));
    }

    @GetMapping("/range/export")
    public ResponseEntity<byte[]> exportAnalyticsByRange(
            @RequestParam("startDate") @DateTimeFormat(pattern = "dd-MM-yyyy") java.time.LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "dd-MM-yyyy") java.time.LocalDate endDate,
            @RequestParam(value = "sentiment", required = false) String sentiment) {
        String normalized = normalizeSentiment(sentiment);
        byte[] csv = analyticsService.exportAnalyticsByDateRange(startDate, endDate, normalized);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sentiment-range.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=utf-8"))
                .body(csv);
    }

    private String normalizeSentiment(String sentiment) {
        if (sentiment == null) {
            return null;
        }
        String trimmed = sentiment.trim();
        if (trimmed.isEmpty() || "Todos".equalsIgnoreCase(trimmed)) {
            return null;
        }
        return trimmed;
    }
}

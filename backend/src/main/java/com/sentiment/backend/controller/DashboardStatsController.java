package com.sentiment.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sentiment.backend.dto.DashboardStatsDTO;
import com.sentiment.backend.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

/**
 * Controller REST para estadísticas del dashboard.
 *
 * Expone métricas agregadas en formato consumible por el frontend.
 */
@RestController
@RequiredArgsConstructor
public class DashboardStatsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/v1/stats")
    public DashboardStatsDTO getStats() {
        // Devuelve métricas agregadas para el dashboard
        return analyticsService.getDashboardStats();
    }
}

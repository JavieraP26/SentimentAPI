package com.sentiment.dashboard.controller;

import com.sentiment.dashboard.dto.AnalyticsResponseDTO;
import com.sentiment.dashboard.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/analytics")
@RequiredArgsConstructor

public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping
    public ResponseEntity<AnalyticsResponseDTO> getAnalytics() {
        AnalyticsResponseDTO response = analyticsService.getAnalytics();
        return ResponseEntity.ok(response);
    }
}

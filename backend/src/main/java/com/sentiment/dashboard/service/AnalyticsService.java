package com.sentiment.dashboard.service;

import com.sentiment.dashboard.dto.AnalyticsResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {
    public AnalyticsResponseDTO getAnalytics() {
        long total = 100;
        long positivos = 60;
        long negativos = 40;
        double promedio = 0.82;

        return new AnalyticsResponseDTO(total, positivos, negativos, promedio);
    }
}

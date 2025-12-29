package com.sentiment.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentResponseDTO {
    private String sentimiento; // Ej: "positivo", "negativo", "neutro" private double score; // Ej: 0.85
}

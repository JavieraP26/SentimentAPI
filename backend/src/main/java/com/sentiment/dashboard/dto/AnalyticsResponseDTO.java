package com.sentiment.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponseDTO {
    private long totalAnalisis;
    private long positivos;
    private long negativos;
    private double promedio;

}

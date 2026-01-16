package com.sentiment.backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar un registro individual en consultas por rango.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentRecordDTO {

    // Fecha del análisis (dd-MM-yyyy)
    private String fecha;
    // Texto original ingresado por el usuario
    private String textoOriginal;
    // Texto interpretado/traducido usado por el modelo
    private String textoInterpretado;
    // Resultado del sentimiento (Positivo/Negativo)
    private String prevision;
    // Probabilidad asociada (0-1)
    private Double probabilidad;
    // Palabras clave detectadas
    private List<String> palabrasClave;
}

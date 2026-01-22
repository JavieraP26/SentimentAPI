package com.sentiment.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para el endpoint POST /v1/sentiment.
 *
 * Representa la solicitud del cliente que contiene el texto a analizar.
 * El texto puede estar en cualquier idioma; el microservicio de Data Science
 * se encarga de detectar el idioma y traducirlo a inglés si es necesario.
 *
 * Validaciones aplicadas:
 * - El texto no puede estar vacío
 * - Debe tener entre 10 y 500 caracteres
 *
 * Ejemplo de uso:
 * {
 *   "text": "El servicio fue excelente y el personal muy amable"
 * }
 */



@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentRequestDTO {

    /**
     * Texto a analizar.
     *
     * Puede estar en cualquier idioma. El sistema detecta automáticamente
     * el idioma y lo traduce al inglés para el análisis ML.
     *
     * Restricciones:
     * - No puede estar vacío (se elimina whitespace antes de validar)
     * - Longitud mínima: 20 caracteres (suficiente contexto para análisis)
     * - Longitud máxima: 500 caracteres
     */

    @NotBlank(message = "El campo 'text' no puede estar vacío.")
    @Size(min = 10, max = 500, message = "El campo 'text' texto debe tener entre 10 y 500 caracteres")
    private String text;
}

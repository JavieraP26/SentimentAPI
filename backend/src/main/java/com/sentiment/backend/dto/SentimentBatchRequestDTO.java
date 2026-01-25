package com.sentiment.backend.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada para análisis batch.
 *
 * Recibe una lista de textos bajo la llave "texts".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentBatchRequestDTO {

    // Lista de textos a analizar en una sola petición
    @NotNull(message = "El campo 'texts' es obligatorio.")
    @NotEmpty(message = "El campo 'texts' no puede estar vacío.")
    @Valid
    private List<
            @NotBlank(message = "Cada elemento de 'texts' es obligatorio.")
            @Size(min = 10, max = 500, message = "Cada texto debe tener entre 10 y 500 caracteres")
            String> texts;
}

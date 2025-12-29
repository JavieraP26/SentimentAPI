package com.sentiment.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentRequestDTO {

    @NotBlank(message = "El texto no puede estar vacío.")
    @Size(min = 5, max = 500, message = "El texto debe tener entre 5 y 500 caracteres")
    private String texto;
}

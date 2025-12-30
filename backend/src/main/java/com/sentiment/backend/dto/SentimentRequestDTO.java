package com.sentiment.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//Agregar el import EnglishText

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentimentRequestDTO {

    @NotBlank(message = "El campo 'text' no puede estar vacío.")
    @Size(min = 10, max = 200, message = "El campo 'text' texto debe tener entre 10 y 200 caracteres")
    //@EnglishText(message = "El campo 'text' debe contener solo texto en inglés")
    private String text;
}

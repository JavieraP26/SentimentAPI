package com.sentiment.backend.controller;


import com.sentiment.backend.dto.SentimentRequestDTO;
import com.sentiment.backend.dto.SentimentResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sentiment")
@Validated
@RequiredArgsConstructor

public class SentimentController {
    private final SentimentService sentimentService; //Inyeccion
    @PostMapping
    public ResponseEntity<SentimentResponseDTO> analyzeSentiment(
            @Valid @RequestBody SentimentRequestDTO request) {

        //Lógica del servicio
        SentimentResponseDTO response = sentimentService.analyze(request);

        return ResponseEntity.ok(response);

    }
}
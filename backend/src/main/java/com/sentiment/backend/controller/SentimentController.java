package com.sentiment.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sentiment.backend.dto.SentimentBatchRequestDTO;
import com.sentiment.backend.dto.SentimentBatchResponseDTO;
import com.sentiment.backend.dto.SentimentRequestDTO;
import com.sentiment.backend.dto.SentimentResponseDTO;
import com.sentiment.backend.service.SentimentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST para análisis de sentimiento.
 *
 * Expone endpoints HTTP para análisis individual y batch,
 * delegando la lógica a la capa de servicio.
 */
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class SentimentController {

    private final SentimentService sentimentService;

    @PostMapping("/sentiment")
    public ResponseEntity<SentimentResponseDTO> analyzeSentiment(
            @Valid @RequestBody SentimentRequestDTO request) {
        // Recibe el texto validado y delega el análisis al servicio
        return ResponseEntity.ok(sentimentService.analyzeSentiment(request));
    }

    @PostMapping("/sentiment/batch")
    public ResponseEntity<SentimentBatchResponseDTO> analyzeBatch(
            @Valid @RequestBody SentimentBatchRequestDTO request) {
        // Recibe lista de textos y devuelve resultados en el mismo orden
        return ResponseEntity.ok(sentimentService.analyzeBatch(request.getTexts()));
    }
}

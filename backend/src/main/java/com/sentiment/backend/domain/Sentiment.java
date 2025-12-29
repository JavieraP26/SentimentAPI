package com.sentiment.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un análisis de sentimiento almacenado en la base de datos.
 * 
 * Esta entidad mapea la tabla "sentiment_analysis" y contiene:
 * - El texto analizado
 * - La clasificación del sentimiento (previsión)
 * - La probabilidad asociada al resultado
 * - Metadatos de auditoría (fecha de creación)
 * 
 * El nombre de los campos (prevision, probabilidad) sigue el contrato del cliente (NoCountry).
 */
@Entity
@Table(name = "sentiment_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sentiment {

    /** Identificador único generado automáticamente por la base de datos */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Texto que fue analizado. Máximo 200 caracteres según restricciones de negocio 
     * NOTA: Se debe confirmar con DS cual es el límite de caracteres para el texto necesarios.
    */
    @Column(nullable = false, length = 200)
    private String text;

    /** Clasificación del sentimiento. Valores posibles: "Positivo", "Neutro", "Negativo" */
    @Column(nullable = false, length = 10)
    private String prevision;

    /** Probabilidad del resultado del análisis. Valor entre 0.0 y 1.0 (inclusive) */
    @Column(nullable = false)
    private Double probabilidad;

    /** Fecha y hora en que se creó el registro. Se establece automáticamente antes de persistir */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Callback de JPA que se ejecuta antes de persistir la entidad.
     * Establece automáticamente la fecha de creación si no está definida.
     */
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

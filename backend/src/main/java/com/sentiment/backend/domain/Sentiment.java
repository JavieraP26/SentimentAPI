package com.sentiment.backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un análisis de sentimiento almacenado en la base de datos.
 *
 * Esta entidad mapea la tabla "sentiment_analysis" y contiene:
 * - El texto original analizado
 * - La clasificación del sentimiento (previsión)
 * - La probabilidad asociada al resultado
 * - El texto traducido al inglés (usado para el análisis ML)
 * - Las palabras clave influyentes (explicabilidad)
 * - Metadatos de auditoría (fecha de creación)
 *
 * El nombre de los campos en español (prevision, probabilidad) sigue el
 * contrato del cliente y facilita la comprensión del modelo de datos.
 */
@Entity
@Table(name = "sentiment_analysis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sentiment {

    /**
     * Identificador único generado automáticamente por la base de datos.
     * Estrategia IDENTITY utiliza auto-increment de PostgreSQL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Texto original que fue analizado.
     *
     * Puede estar en cualquier idioma (español, inglés, etc.).
     *
     * Este es el texto tal como el usuario lo envió, antes de cualquier
     * procesamiento o traducción.
     */
    @Column(nullable = false, length = 500)
    private String text;

    /**
     * Clasificación del sentimiento en español.
     *
     * Valores posibles: "Positivo", "Negativo", "Desconocido"
     * Longitud máxima de 12 caracteres cubre el fallback "Desconocido".
     */
    @Column(nullable = false, length = 12)
    private String prevision;

    /**
     * Probabilidad del resultado del análisis.
     *
     * Valor entre 0.0 y 1.0 (inclusive) que representa la confianza
     * del modelo ML en la predicción.
     *
     * Se almacena como DOUBLE en PostgreSQL para precisión decimal.
     */
    @Column(nullable = false)
    private Double probabilidad;

    /**
     * Fecha y hora en que se creó el registro.
     *
     * Se establece automáticamente antes de persistir mediante @PrePersist.
     * Campo no actualizable (updatable = false) para mantener auditoría.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Texto traducido al inglés por el microservicio de Data Science.
     *
     * Contiene la versión en inglés del texto original que fue usada
     * para el análisis ML. Si el texto original ya estaba en inglés,
     * este campo contiene el mismo valor.
     *
     * Este campo permite:
     * - Verificar la calidad de la traducción automática
     * - Debugging de resultados inesperados
     * - Analytics sobre traducciones frecuentes
     *
     * Longitud máxima de 500 caracteres (igual que el texto original).
     */
    @Column(length = 500)
    private String textoTraducido;

    /**
     * Palabras clave que influyeron en la predicción (explicabilidad).
     *
     * NOTA: El formato de almacenamiento está pendiente de decisión del equipo.
     *
     * Opciones consideradas:
     * 1. CSV (actual): "excellent, helpful, service"
     *    - Permite búsquedas básicas con LIKE en SQL
     * 2. JSON: ["excellent", "helpful", "service"]
     *    - Más estructurado, requiere parsing JSON
     * 3. Tabla separada: TopFeature(id, sentimentId, palabra)
     *    - Mayor normalización, más complejo
     *
     * TODO: Confirmar formato definitivo con el equipo antes de integración con dashboard.
     */
    @Column(length = 500)
    private String palabrasClave;

    /**
     * Callback de JPA que se ejecuta antes de persistir la entidad.
     *
     * Establece automáticamente la fecha de creación si no está definida.
     * Esto garantiza que siempre haya un timestamp de auditoría.
     */
    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

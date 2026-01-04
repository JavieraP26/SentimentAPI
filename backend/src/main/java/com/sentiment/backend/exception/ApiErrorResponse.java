package com.sentiment.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO estándar para respuestas de error de la API.
 * Proporciona una estructura consistente para todos los errores manejados globalmente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Omite campos null en la serialización JSON
public class ApiErrorResponse {
    /** Fecha y hora en que ocurrió el error */
    private LocalDateTime timestamp;
    
    /** Código de estado HTTP (ej: 400, 500) */
    private int status;
    
    /** Descripción corta del tipo de error HTTP (ej: "Bad Request", "Internal Server Error") */
    private String error;
    
    /** Mensaje descriptivo del error para el cliente */
    private String message;
    
    /** Ruta del endpoint donde ocurrió el error */
    private String path;
    
    /** Detalles adicionales del error (opcional). 
     *  Útil para errores de validación donde se mapean campos y sus mensajes específicos */
    private Map<String, Object> details;
}


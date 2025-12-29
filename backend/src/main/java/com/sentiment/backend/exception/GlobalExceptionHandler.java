package com.sentiment.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Intercepta excepciones no capturadas y las convierte en respuestas HTTP estandarizadas.
 * 
 * @RestControllerAdvice permite que este componente capture excepciones de todos los controllers.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación de argumentos de métodos (@Valid en DTOs).
     * Se activa cuando un parámetro anotado con @Valid falla la validación.
     * 
     * @param ex Excepción con los errores de validación por campo
     * @param request Request HTTP para obtener la URI del endpoint
     * @return Respuesta HTTP 400 con detalles de validación por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        // Extrae los errores de validación por campo y los mapea
        Map<String, Object> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            details.put(error.getField(), error.getDefaultMessage());
        });
        
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error de validación en los datos de entrada")
                .path(request.getRequestURI())
                .details(details)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja violaciones de restricciones de validación (ej: @Validated en parámetros de query/path).
     * Se activa cuando se validan parámetros de URL o parámetros de método directamente.
     * 
     * @param ex Excepción con las violaciones de restricciones
     * @param request Request HTTP para obtener la URI del endpoint
     * @return Respuesta HTTP 400 con detalles de las violaciones
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {
        
        log.warn("Constraint violation: {}", ex.getMessage());
        
        // Mapea cada violación de restricción a detalles del error
        Map<String, Object> details = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            details.put(field, violation.getMessage());
        }
        
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Violación de restricciones de validación")
                .path(request.getRequestURI())
                .details(details)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja errores al deserializar el cuerpo de la petición HTTP.
     * Se activa cuando el JSON es inválido, está mal formado o no coincide con el DTO esperado.
     * 
     * @param ex Excepción con el error de deserialización
     * @param request Request HTTP para obtener la URI del endpoint
     * @return Respuesta HTTP 400 indicando error en el formato del JSON
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        
        log.warn("Message not readable: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Error al leer el cuerpo de la petición. Verifique el formato JSON.")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Maneja errores cuando el servicio de Data Science no está disponible.
     * Se activa cuando se lanza ServiceUnavailableException, típicamente desde
     * la capa de servicio cuando hay problemas de conectividad con el servicio externo, pero eso lo revisaremos hoy.
     * 
     * @param ex Excepción de servicio no disponible
     * @param request Request HTTP para obtener la URI del endpoint
     * @return Respuesta HTTP 503 indicando que el servicio no está disponible
     */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleServiceUnavailable(
            ServiceUnavailableException ex,
            HttpServletRequest request) {
        
        log.error("Service unavailable: {}", ex.getMessage());
        
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase())
                .message(ex.getMessage() != null && !ex.getMessage().isEmpty() 
                    ? ex.getMessage() 
                    : "El servicio de análisis de sentimiento no está disponible en este momento. Por favor, intenta más tarde.")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * Handler genérico para cualquier excepción no capturada por handlers específicos.
     * Actúa como fallback para errores inesperados del sistema.
     * 
     * IMPORTANTE: Este handler debe ser el último en el orden de evaluación.
     * 
     * @param ex Excepción no manejada
     * @param request Request HTTP para obtener la URI del endpoint
     * @return Respuesta HTTP 500 con mensaje genérico (no expone detalles internos al cliente)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected error: ", ex);
        
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Error interno del servidor")
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}


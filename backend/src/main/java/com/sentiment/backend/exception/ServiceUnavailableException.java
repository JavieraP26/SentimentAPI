package com.sentiment.backend.exception;

/**
 * Excepción lanzada cuando el servicio de Data Science no está disponible.
 * 
 * Esta excepción debe ser lanzada cuando:
 * - El servicio de análisis de sentimiento (DS) no responde
 * - El servicio está temporalmente fuera de servicio
 * - Hay problemas de conectividad con el servicio externo
 * 
 * El GlobalExceptionHandler captura esta excepción y devuelve un HTTP 503 (Service Unavailable).
 * 
 * @see GlobalExceptionHandler#handleServiceUnavailable(ServiceUnavailableException, HttpServletRequest)
 */
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}


package com.sentiment.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Anotación de validación personalizada para verificar que un texto esté escrito en inglés.
 * 
 * Esta validación es crítica porque el modelo de Data Science está entrenado exclusivamente
 * con datos en inglés. Textos en otros idiomas pueden producir resultados incorrectos.
 * 
 * Uso:
 * <pre>
 * {@code
 * public class SentimentRequest {
 *     @EnglishText
 *     private String text;
 * }
 * }
 * </pre>
 * 
 * La validación es realizada por EnglishTextValidator que utiliza la librería Lingua
 * para detectar el idioma del texto.
 * 
 * @see EnglishTextValidator
 */
@Documented
@Constraint(validatedBy = EnglishTextValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnglishText {
    /** Mensaje de error por defecto cuando la validación falla */
    String message() default "El texto debe estar escrito en inglés.";
    
    /** Grupos de validación (para validación condicional) */
    Class<?>[] groups() default {};
    
    /** Payload para metadata adicional (severidad, etc.) */
    Class<? extends Payload>[] payload() default {};
}

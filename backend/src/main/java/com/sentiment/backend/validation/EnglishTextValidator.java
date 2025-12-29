package com.sentiment.backend.validation;

import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static com.github.pemistahl.lingua.api.Language.*;

/**
 * Validador que implementa la lógica de validación para la anotación @EnglishText.
 * 
 * Utiliza la librería Lingua para detectar el idioma del texto. El detector está
 * configurado para reconocer inglés, español y portugués (los idiomas más comunes
 * en el contexto del proyecto).
 * 
 * Estrategia de validación:
 * - Si el texto es null o vacío, retorna true (deja que @NotBlank maneje esto)
 * - Si el texto es muy corto (< 20 caracteres), retorna true (best effort, Lingua
 *   puede fallar con textos muy cortos)
 * - Para textos más largos, detecta el idioma y valida que sea inglés
 * 
 * @see EnglishText
 */
public class EnglishTextValidator implements ConstraintValidator<EnglishText, String> {

    /** Longitud mínima de caracteres para que la detección de idioma sea confiable */
    private static final int MIN_CHARS_FOR_DETECTION = 20;

    /** Detector de idioma configurado para inglés, español y portugués */
    private final LanguageDetector detector =
            LanguageDetectorBuilder.fromLanguages(ENGLISH, SPANISH, PORTUGUESE).build();

    /**
     * Valida que el texto proporcionado esté escrito en inglés.
     * 
     * @param value Texto a validar
     * @param context Contexto de validación (no utilizado en esta implementación)
     * @return true si el texto es válido (inglés) o no puede ser validado (null/vacío/corto),
     *         false si el texto está en otro idioma
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Deja que @NotBlank y @Size manejen null/vacío/largo
        if (value == null || value.isBlank()) return true;

        // Para textos muy cortos, Lingua puede fallar (best effort)
        // Permitimos textos cortos para no ser demasiado restrictivos
        if (value.trim().length() < MIN_CHARS_FOR_DETECTION) return true;

        // Detecta el idioma y valida que sea inglés
        Language detected = detector.detectLanguageOf(value);
        return detected == ENGLISH;
    }
}

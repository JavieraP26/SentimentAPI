package com.sentiment.backend.repository;

import com.sentiment.backend.domain.Sentiment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad Sentiment.
 * 
 * Extiende JpaRepository proporcionando operaciones CRUD estándar:
 * - save(), findById(), findAll(), delete(), etc.
 * 
 * Spring Data JPA implementa automáticamente esta interfaz en tiempo de ejecución.
 * No requiere implementación manual.
 * 
 * @param <Sentiment> Tipo de entidad
 * @param <Long> Tipo del identificador (ID)
 */
public interface SentimentRepository extends JpaRepository<Sentiment, Long> {}

package com.sentiment.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sentiment.backend.domain.Sentiment;

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
public interface SentimentRepository extends JpaRepository<Sentiment, Long> {

    // Cuenta registros por tipo de prevision (Positivo/Negativo)
    long countByPrevision(String prevision);

    // Calcula el promedio global de probabilidad
    @Query("select avg(s.probabilidad) from Sentiment s")
    Double getAverageProbabilidad();

    // Obtiene registros por rango de fechas (inclusive)
    List<Sentiment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Obtiene registros por rango de fechas (paginado)
    Page<Sentiment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // Conteo por rango de fechas
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Obtiene registros por rango y sentimiento (paginado)
    @Query("""
            select s from Sentiment s
            where s.createdAt between :start and :end
              and s.prevision in :sentiments
            order by s.createdAt desc
            """)
    Page<Sentiment> findByRangeAndSentiment(
            LocalDateTime start,
            LocalDateTime end,
            List<String> sentiments,
            Pageable pageable);

    // Obtiene registros por rango y sentimiento (sin paginar, para resumen/export)
    @Query("""
            select s from Sentiment s
            where s.createdAt between :start and :end
              and s.prevision in :sentiments
            order by s.createdAt desc
            """)
    List<Sentiment> findAllByRangeAndSentiment(
            LocalDateTime start,
            LocalDateTime end,
            List<String> sentiments);

    // Conteo por rango y sentimiento
    @Query("""
            select count(s) from Sentiment s
            where s.createdAt between :start and :end
              and s.prevision in :sentiments
            """)
    long countByRangeAndSentiment(
            LocalDateTime start,
            LocalDateTime end,
            List<String> sentiments);
}

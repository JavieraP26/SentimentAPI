package com.sentiment.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sentiment.backend.dto.SentimentRequestDTO;
import com.sentiment.backend.dto.SentimentResponseDTO;
import com.sentiment.backend.service.CsvBatchService;
import com.sentiment.backend.service.SentimentService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

/**
 * Controller MVC para el dashboard.
 *
 * Sirve la vista principal y maneja formularios de texto y CSV.
 */
@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/dashboard")
public class DashboardController {

    private final SentimentService sentimentService;
    private final CsvBatchService csvBatchService;

    @GetMapping
    public String index() {
        // Renderiza la vista principal del dashboard
        return "index";
    }

    @PostMapping("/sentiment")
    public String analizar(
            @RequestParam("texto")
            @NotBlank(message = "El campo 'text' no puede estar vacío.")
            @Size(min = 20, max = 500, message = "El campo 'text' debe tener entre 20 y 500 caracteres")
            String texto,
            Model model) {
        // Envía el texto al servicio y obtiene la respuesta formateada
        SentimentResponseDTO respuesta = sentimentService.analyzeSentiment(new SentimentRequestDTO(texto));

        // Poblar el modelo para mostrar resultados en la vista
        model.addAttribute("resultado", respuesta.getPrevision());
        model.addAttribute("probabilidad", String.format("%.2f%%", respuesta.getProbabilidad() * 100));
        model.addAttribute("textoOriginal", texto);
        model.addAttribute("textoInterpretado", respuesta.getTextoTraducido());
        model.addAttribute("palabrasClave", respuesta.getPalabrasClave());

        return "index";
    }

    @PostMapping("/upload")
    public String subirArchivo(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        // Validación simple de archivo antes de procesar
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "Por favor selecciona un archivo.");
            return "redirect:/v1/dashboard";
        }

        // Procesa el CSV usando el flujo batch
        int cantidad = csvBatchService.processCsv(file);

        // Mensajes flash para feedback en la vista
        if (cantidad > 0) {
            redirectAttributes.addFlashAttribute("mensaje",
                    "¡Éxito! Se han procesado y guardado " + cantidad + " comentarios.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "alert-success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Hubo un error procesando el archivo.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "alert-danger");
        }

        return "redirect:/v1/dashboard";
    }
}

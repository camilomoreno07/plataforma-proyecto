package com.proyectogrado.plataforma.progress.Controller;

import com.proyectogrado.plataforma.progress.Model.Progress;
import com.proyectogrado.plataforma.progress.Service.ProgressService;
import com.proyectogrado.plataforma.progress.dto.EvaluationGradeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    // 🔸 POST: Guardar evaluación
    @PostMapping("/grade-evaluation")
    public ResponseEntity<?> gradeEvaluation(@RequestBody EvaluationGradeDTO dto) {
        try {
            progressService.gradeEvaluation(dto);
            return ResponseEntity.ok("Evaluación registrada correctamente");
        } catch (Exception e) {
            e.printStackTrace(); // log completo
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }


    @GetMapping("/student")
    public ResponseEntity<Progress> getStudentProgress(
            @RequestParam String courseId,
            @RequestParam String studentEmail
    ) {
        Progress progress = progressService.findByCourseIdAndStudentEmail(courseId, studentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Progreso no encontrado"));

        return ResponseEntity.ok(progress);
    }

}


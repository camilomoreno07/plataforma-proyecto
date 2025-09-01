package com.proyectogrado.plataforma.progress.Controller;

import com.proyectogrado.plataforma.progress.Model.Progress;
import com.proyectogrado.plataforma.progress.Service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/progress")
public class ProgressController
{
    @Autowired
    private ProgressService progressService;

    /** Create or update progress */
    @PostMapping
    public ResponseEntity<Progress> saveProgress(@RequestBody Progress progress) {
        Progress saved = progressService.saveProgress(progress);
        return ResponseEntity.ok(saved);
    }

    /** Get progress by ID */
    @GetMapping("/{id}")
    public ResponseEntity<Progress> getById(@PathVariable String id) {
        return progressService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Get progress by courseId and studentId */
    @GetMapping("/course/{courseId}/student/{studentId}")
    public ResponseEntity<Progress> getByCourseAndStudent(
            @PathVariable String courseId,
            @PathVariable String studentId) {
        Optional<Progress> progress = progressService.getByCourseAndStudent(courseId, studentId);
        return progress.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Delete progress by ID */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!progressService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        progressService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** Get progress percentage by courseId and studentId */
    @GetMapping("/course/{courseId}/student/{studentId}/percentage")
    public ResponseEntity<Double> getPercentageByCourseAndStudent(
            @PathVariable String courseId,
            @PathVariable String studentId) {
        return progressService.getPercentageByCourseAndStudent(courseId, studentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Calculate percentage directly */
    @GetMapping("/{id}/percentage")
    public ResponseEntity<Double> getPercentage(@PathVariable String id) {
        return progressService.getPercentage(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
package com.proyectogrado.plataforma.progress.Service;


import com.proyectogrado.plataforma.course.Model.Course;
import com.proyectogrado.plataforma.course.Repository.CourseRepository;
import com.proyectogrado.plataforma.progress.Model.Progress;
import com.proyectogrado.plataforma.progress.Model.ProgressMoment;
import com.proyectogrado.plataforma.progress.Repository.ProgressRepository;
import com.proyectogrado.plataforma.progress.dto.EvaluationGradeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProgressService {

    @Autowired
    private ProgressRepository repository;

    @Autowired
    private CourseRepository courseRepository;

    // Guardar o actualizar progreso
    public Progress saveProgress(Progress progress) {
        return repository.save(progress);
    }

    // Buscar progreso de un estudiante en un curso
    public Optional<Progress> findByCourseIdAndStudentEmail(String courseId, String studentEmail) {
        return repository.findByCourseIdAndStudentEmail(courseId, studentEmail);
    }

    // Traer todos los progresos de un curso (ej: para que el profe vea)
    public List<Progress> findAllByCourseId(String courseId) {
        return repository.findAllByCourseId(courseId);
    }

    // Borrar progreso de un estudiante
    public void deleteProgress(String id) {
        repository.deleteById(id);
    }

    public void updateSectionStatus(String courseId, String studentEmail, String moment, String sectionType, boolean completed) {
        Progress progress = repository.findByCourseIdAndStudentEmail(courseId, studentEmail)
                .orElse(new Progress());

        progress.setCourseId(courseId);
        progress.setStudentEmail(studentEmail);

        if (progress.getMoments() == null) progress.setMoments(new HashMap<>());
        ProgressMoment momentProgress = progress.getMoments().getOrDefault(moment, new ProgressMoment());

        switch (sectionType) {
            case "instructions" -> momentProgress.setInstructionsCompleted(completed);
            case "contents" -> {
                // marcamos todos los contenidos igual que 'completed'
                for (String contentTitle : momentProgress.getContentsCompleted().keySet()) {
                    momentProgress.getContentsCompleted().put(contentTitle, completed);
                }
            }
            case "evaluations" -> {
                // si quisiéramos marcar todas las respuestas como vacías o nulas:
                for (String question : momentProgress.getEvaluationsAnswers().keySet()) {
                    momentProgress.getEvaluationsAnswers().put(question, null);
                }
            }
        }

        progress.getMoments().put(moment, momentProgress);
        repository.save(progress);
    }

    public void gradeEvaluation(EvaluationGradeDTO dto) {
        Progress progress = repository.findByCourseIdAndStudentEmail(dto.getCourseId(), dto.getStudentEmail())
                .orElseGet(() -> {
                    Progress newProgress = new Progress();
                    newProgress.setCourseId(dto.getCourseId());
                    newProgress.setStudentEmail(dto.getStudentEmail());
                    newProgress.setProgressStatus("En progreso");
                    newProgress.setStartedAt(LocalDateTime.now());
                    newProgress.setMoments(new HashMap<>());
                    return newProgress;
                });

        if (progress.getMoments() == null) {
            progress.setMoments(new HashMap<>());
        }

        Map<String, ProgressMoment> moments = progress.getMoments();

        ProgressMoment moment = moments.get(dto.getMoment());
        if (moment == null) {
            moment = new ProgressMoment(); // ← aquí evaluationsAnswers ya viene inicializado
        }

        // 🔒 Protección doble por si fue sobreescrito por deserialización o Mongo
        if (moment.getEvaluationsAnswers() == null) {
            moment.setEvaluationsAnswers(new HashMap<>());
        }

        moment.getEvaluationsAnswers().put(dto.getQuestion(), dto.getAnswer()); // ← Aquí explotaba

        moment.setEvaluationScore(dto.getScore());

        moments.put(dto.getMoment(), moment);
        progress.setMoments(moments);

        // Calcular promedio
        List<Integer> scores = moments.values().stream()
                .map(ProgressMoment::getEvaluationScore)
                .filter(Objects::nonNull)
                .toList();

        if (!scores.isEmpty()) {
            int sum = scores.stream().mapToInt(Integer::intValue).sum();
            int avg = Math.round((float) sum / scores.size());
            progress.setCurrentStep(avg);
        }

        repository.save(progress);
    }

}

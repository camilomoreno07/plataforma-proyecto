package com.proyectogrado.plataforma.progress.Model;


import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "student_course_progress")
public class Progress {
    @Id
    private String id;
    private String courseId;
    private String studentEmail;
    private String progressStatus; // "pendiente", "en progreso", "completado"
    private List<String> completedEvaluations;
    private Integer currentStep;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}

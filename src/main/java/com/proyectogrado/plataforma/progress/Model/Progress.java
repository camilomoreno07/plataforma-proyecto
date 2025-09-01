package com.proyectogrado.plataforma.progress.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "student_course_progress")
public class Progress
{
    @Id
    private String id;

    private String courseId;
    private String studentId;

    private MomentProgress aulaInvertida;
    private MomentProgress tallerHabilidad;
    private MomentProgress actividadExperiencial;

    public double getPercentage()
    {
        return List.of(aulaInvertida, tallerHabilidad, actividadExperiencial).stream()
                .mapToDouble(MomentProgress::getPercentage)
                .average()
                .orElse(0.0);
    }
}

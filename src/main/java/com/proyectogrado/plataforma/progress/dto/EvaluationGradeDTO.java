package com.proyectogrado.plataforma.progress.dto;

import lombok.Data;

@Data
public class EvaluationGradeDTO {
    private String courseId;
    private String studentEmail;
    private String moment;   // "beforeClass", "duringClass", etc.
    private String question;
    private String answer;
    private Integer score;
}

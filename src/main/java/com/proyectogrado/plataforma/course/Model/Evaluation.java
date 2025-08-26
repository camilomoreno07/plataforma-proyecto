package com.proyectogrado.plataforma.course.Model;

import lombok.Data;

import java.util.List;

@Data
public class Evaluation {
    private String questionType;   // "OPEN", "MC3", "MC5"
    private String question;
    private String correctAnswer;
    private Integer time;
    private List<String> options;  // null o vacío si es OPEN
}
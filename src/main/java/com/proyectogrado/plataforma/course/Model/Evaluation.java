package com.proyectogrado.plataforma.course.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
public class Evaluation {

    private String question;
    private String correctAnswer;
    private Integer time;

}

package com.proyectogrado.plataforma.course.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "evaluations")
public class Evaluation {

    @Id
    private String evaluationId;
    private String question;
    private String questionDescription;
    private Integer time;

}

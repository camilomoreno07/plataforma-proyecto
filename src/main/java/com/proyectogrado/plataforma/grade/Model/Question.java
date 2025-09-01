package com.proyectogrado.plataforma.grade.Model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "question")
public class Question {

    private String response;
    private String feedback;

}

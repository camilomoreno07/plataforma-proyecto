package com.proyectogrado.plataforma.grade.Model;


import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "classmoment")
public class ClassMoment {

    private List<Question> questions;
    private String grade;
}

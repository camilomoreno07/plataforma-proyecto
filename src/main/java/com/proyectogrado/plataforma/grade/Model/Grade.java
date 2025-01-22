package com.proyectogrado.plataforma.grade.Model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "grades")
public class Grade {

    @Id
    private String gradeId;
    private String studentId;
    private String evaluationType;
    private String result;

}

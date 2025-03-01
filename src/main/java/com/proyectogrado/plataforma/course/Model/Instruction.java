package com.proyectogrado.plataforma.course.Model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "instructions")
public class Instruction {

    @Id
    private String instructionId;
    private String instructionTitle;
    private String instructionDescription;
    private Integer time;
    private List<String> steps;

}

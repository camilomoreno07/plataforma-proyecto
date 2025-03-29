package com.proyectogrado.plataforma.course.Model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "classmoment")
public class ClassMoment {

    private Instruction instructions;
    private List<Content> contents;
    private List<Evaluation> evaluations;

}

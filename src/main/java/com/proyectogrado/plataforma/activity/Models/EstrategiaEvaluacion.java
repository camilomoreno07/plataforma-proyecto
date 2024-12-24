package com.proyectogrado.plataforma.activity.Models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "estrategia_evaluacion")
public class EstrategiaEvaluacion {

    @Id
    private String id;
    private String estrategia;

}

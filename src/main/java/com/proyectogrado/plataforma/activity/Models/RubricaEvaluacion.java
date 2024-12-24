package com.proyectogrado.plataforma.activity.Models;


import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "rubrica_evaluacion")
public class RubricaEvaluacion {
    @Id
    private String id;
    private String titulo;
    private String resumen;
    private List<String> competencias;
    private List<String> niveles;
}

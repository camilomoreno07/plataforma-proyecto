package com.proyectogrado.plataforma.activity.Models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "activities")
public class Activity {

    @Id
    private String activityId;
    private String titulo;
    private String resumen;
    private int tiempoEstimado;

    private PreparacionTeorica preparacionTeorica;
    private PresentacionActiva presentacionActiva;
    private EjercicioPractico ejercicioPractico;
    private RubricaEvaluacion rubricaEvaluacion;

}

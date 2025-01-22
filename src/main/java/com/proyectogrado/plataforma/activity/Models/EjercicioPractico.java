package com.proyectogrado.plataforma.activity.Models;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ejercicio_practico")
public class EjercicioPractico {

    @Id
    private String id;
    private String titulo;
    private String resumen;
    private int tiempoEstimado;
    private int dificultadEjercicio;

    private ContenidoProcedural contenidoProcedural;
    private ContenidoSoporte contenidoSoporte;
    private EstrategiaEvaluacion estrategiaEvaluacion;

}

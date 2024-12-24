package com.proyectogrado.plataforma.activity.Models;


import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "preparacion_teorica")
public class PreparacionTeorica {

    @Id
    private String id;
    private String titulo;
    private String resumen;
    private int tiempoEstimado;
    private int cargaCognitiva;

    private ContenidoProcedural contenidoProcedural;
    private ContenidoSoporte contenidoSoporte;
    private EstrategiaEvaluacion estrategiaEvaluacion;

}

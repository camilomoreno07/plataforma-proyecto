package com.proyectogrado.plataforma.activity.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "contenido_procedural")
public class ContenidoProcedural {

    @Id
    private String id;
    private int dificultadEjecucion;
    private List<String> pasos;

}

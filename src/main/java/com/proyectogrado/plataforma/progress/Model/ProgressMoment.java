package com.proyectogrado.plataforma.progress.Model;

import lombok.Data;

import java.util.Map;

@Data
public class ProgressMoment {
    private boolean instructionsCompleted;
    private boolean contentsCompleted;
    private boolean evaluationsCompleted;

    // También podrías guardar cosas extras, por ejemplo notas, respuestas, etc.
    private Map<String, Object> extraData;
}
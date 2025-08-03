package com.proyectogrado.plataforma.progress.Model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ProgressMoment {
    private boolean instructionsCompleted;
    private Map<String, Boolean> contentsCompleted;
    private Map<String, String> evaluationsAnswers;

    private Map<String, Object> extraData;
    private Integer evaluationScore;
    private String feedback;

    public ProgressMoment() {
        this.instructionsCompleted = false;
        this.contentsCompleted = new HashMap<>();
        this.evaluationsAnswers = new HashMap<>();
    }

}
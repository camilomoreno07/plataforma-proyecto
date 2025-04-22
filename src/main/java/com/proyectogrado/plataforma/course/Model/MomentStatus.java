package com.proyectogrado.plataforma.course.Model;

import lombok.Data;

@Data
public class MomentStatus {
    private Status beforeClass;
    private Status duringClass;
    private Status afterClass;

    @Data
    public static class Status {
        private String instructions;
        private String contents;
        private String evaluations;
    }
}

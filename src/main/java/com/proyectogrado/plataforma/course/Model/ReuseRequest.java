package com.proyectogrado.plataforma.course.Model;

import lombok.Data;

@Data
public class ReuseRequest {
    private String sourceCourseId;
    private String targetCourseId;
    private Reuse reuse;

    @Data
    public static class Reuse {
        private Stage beforeClass;
        private Stage duringClass;
        private Stage afterClass;
    }

    @Data
    public static class Stage {
        private boolean instructions;
        private boolean contents;
        private boolean evaluations;
    }
}

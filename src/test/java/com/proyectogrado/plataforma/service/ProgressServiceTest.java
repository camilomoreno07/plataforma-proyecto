package com.proyectogrado.plataforma.service;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.progress.Model.MomentProgress;
import com.proyectogrado.plataforma.progress.Model.Progress;
import com.proyectogrado.plataforma.progress.Service.ProgressService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Import(EmbedMongoConfig.class)
class ProgressServiceTest
{

    @Autowired
    private ProgressService progressService;

    @Test
    void saveProgress() {
        Progress progress = new Progress();
        progress.setCourseId("course-123");
        progress.setStudentId("student-save@test.com");
        progress.setAulaInvertida(new MomentProgress(3));
        progress.setTallerHabilidad(new MomentProgress(2));
        progress.setActividadExperiencial(new MomentProgress(4));

        progressService.saveProgress(progress);

        Optional<Progress> saved = progressService.getByCourseAndStudent(
                "course-123", "student-save@test.com"
        );
        Assertions.assertTrue(saved.isPresent());
        Assertions.assertEquals(0.0, saved.get().getPercentage());

        progressService.deleteById(progress.getId());
    }

    @Test
    void getByCourseAndStudent() {
        Progress progress = new Progress();
        progress.setCourseId("course-456");
        progress.setStudentId("student@email.com");
        progress.setAulaInvertida(new MomentProgress(2));
        progress.setTallerHabilidad(new MomentProgress(2));
        progress.setActividadExperiencial(new MomentProgress(2));

        progressService.saveProgress(progress);

        Optional<Progress> found = progressService.getByCourseAndStudent("course-456", "student@email.com");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals(0.0, found.get().getPercentage());

        progressService.deleteById(progress.getId());
    }

    @Test
    void findAllByCourseId() {
        String courseId = "course-789";
        int initialSize = progressService.getAllByCourseId(courseId).size();

        Progress progress = new Progress();
        progress.setCourseId(courseId);
        progress.setStudentId("student-all@test.com");
        progress.setAulaInvertida(new MomentProgress(1));
        progress.setTallerHabilidad(new MomentProgress(1));
        progress.setActividadExperiencial(new MomentProgress(1));

        progressService.saveProgress(progress);

        List<Progress> all = progressService.getAllByCourseId(courseId);
        Assertions.assertEquals(initialSize + 1, all.size());

        progressService.deleteById(progress.getId());
    }

    @Test
    void deleteProgress() {
        Progress progress = new Progress();
        progress.setCourseId("course-delete");
        progress.setStudentId("student-delete@test.com");
        progress.setAulaInvertida(new MomentProgress(1));
        progress.setTallerHabilidad(new MomentProgress(1));
        progress.setActividadExperiencial(new MomentProgress(1));

        progressService.saveProgress(progress);

        Optional<Progress> existing = progressService.getByCourseAndStudent(
                "course-delete", "student-delete@test.com"
        );
        Assertions.assertTrue(existing.isPresent());

        progressService.deleteById(progress.getId());

        Optional<Progress> deleted = progressService.getByCourseAndStudent(
                "course-delete", "student-delete@test.com"
        );
        Assertions.assertFalse(deleted.isPresent());
    }
}
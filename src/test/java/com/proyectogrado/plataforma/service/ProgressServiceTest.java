package com.proyectogrado.plataforma.service;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.progress.Model.Progress;
import com.proyectogrado.plataforma.progress.Service.ProgressService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Import(EmbedMongoConfig.class)
class ProgressServiceTest
{
    @Autowired
    private ProgressService progressService;

    @Test
    void saveProgress()
    {
        Progress progress = new Progress();
        progress.setCourseId("course-123");
        progress.setStudentEmail("student-save@test.com");
        progress.setProgressStatus("pendiente");

        progressService.saveProgress(progress);

        Optional<Progress> saved = progressService.findByCourseIdAndStudentEmail(
                "course-123", "student-save@test.com"
        );
        Assertions.assertTrue(saved.isPresent());
        Assertions.assertEquals("pendiente", saved.get().getProgressStatus());

        progressService.deleteProgress(progress.getId());
    }

    @Test
    void findByCourseIdAndStudentEmail()
    {
        Progress progress = new Progress();
        progress.setCourseId("course-456");
        progress.setStudentEmail("student@email.com");
        progress.setProgressStatus("en progreso");

        progressService.saveProgress(progress);

        Optional<Progress> found = progressService.findByCourseIdAndStudentEmail("course-456", "student@email.com");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("en progreso", found.get().getProgressStatus());

        progressService.deleteProgress(progress.getId());
    }

    @Test
    void findAllByCourseId()
    {
        String courseId = "course-789";
        int initialSize = progressService.findAllByCourseId(courseId).size();

        Progress progress = new Progress();
        progress.setCourseId(courseId);
        progress.setProgressStatus("completado");

        progressService.saveProgress(progress);

        List<Progress> all = progressService.findAllByCourseId(courseId);
        Assertions.assertEquals(initialSize + 1, all.size());

        progressService.deleteProgress(progress.getId());
    }

    @Test
    void deleteProgress()
    {
        Progress progress = new Progress();
        progress.setCourseId("course-delete");
        progress.setStudentEmail("student-delete@test.com");
        progress.setProgressStatus("pendiente");

        progressService.saveProgress(progress);

        Optional<Progress> existing = progressService.findByCourseIdAndStudentEmail(
                "course-delete", "student-delete@test.com"
        );
        Assertions.assertTrue(existing.isPresent());

        progressService.deleteProgress(progress.getId());

        Optional<Progress> deleted = progressService.findByCourseIdAndStudentEmail(
                "course-delete", "student-delete@test.com"
        );
        Assertions.assertFalse(deleted.isPresent());
    }
}
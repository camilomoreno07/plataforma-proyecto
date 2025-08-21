package com.proyectogrado.plataforma.service;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.grade.Model.Grade;
import com.proyectogrado.plataforma.grade.Service.GradeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@SpringBootTest
@Import(EmbedMongoConfig.class)
class GradeServiceTest
{
    @Autowired
    private GradeService gradeService;

    @Test
    void findAll()
    {
        int initialSize = gradeService.findAll().size();

        Grade newGrade = new Grade();
        newGrade.setStudentId("student1@gmail.com");
        newGrade.setEvaluationType("Parcial");
        newGrade.setResult("4.0");

        gradeService.save(newGrade);

        Assertions.assertEquals(initialSize + 1, gradeService.findAll().size());

        gradeService.deleteById(newGrade.getGradeId());

        Assertions.assertEquals(initialSize, gradeService.findAll().size());
    }

    @Test
    void findById()
    {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student2@gmail.com");
        newGrade.setEvaluationType("Final");
        newGrade.setResult("3.4");

        gradeService.save(newGrade);

        Grade foundGrade = gradeService.findById(newGrade.getGradeId()).orElse(null);
        Assertions.assertNotNull(foundGrade);
        Assertions.assertEquals(newGrade.getResult(), foundGrade.getResult());

        gradeService.deleteById(newGrade.getGradeId());
    }

    @Test
    void save()
    {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student3@gmail.com");
        newGrade.setEvaluationType("Quiz");
        newGrade.setResult("4.1");

        gradeService.save(newGrade);

        Optional<Grade> savedGrade = gradeService.findById(newGrade.getGradeId());
        Assertions.assertTrue(savedGrade.isPresent());

        gradeService.deleteById(newGrade.getGradeId());
    }

    @Test
    void deleteById()
    {
        Grade newGrade = new Grade();
        newGrade.setStudentId("student4@gmail.com");
        newGrade.setEvaluationType("Taller");
        newGrade.setResult("3.8");

        gradeService.save(newGrade);

        int sizeBefore = gradeService.findAll().size();

        gradeService.deleteById(newGrade.getGradeId());

        Assertions.assertEquals(sizeBefore - 1, gradeService.findAll().size());
    }
}
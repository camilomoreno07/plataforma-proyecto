package com.proyectogrado.plataforma.service;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.grade.Model.ClassMoment;
import com.proyectogrado.plataforma.grade.Model.Grade;
import com.proyectogrado.plataforma.grade.Model.Question;
import com.proyectogrado.plataforma.grade.Service.GradeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Import(EmbedMongoConfig.class)
class GradeServiceTest
{

    @Autowired
    private GradeService gradeService;

    private Grade buildGrade(String studentId) {
        Question q1 = new Question();
        q1.setResponse("Answer 1");
        q1.setFeedback("Feedback 1");

        Question q2 = new Question();
        q2.setResponse("Answer 2");
        q2.setFeedback("Feedback 2");

        Question q3 = new Question();
        q3.setResponse("Answer 3");
        q3.setFeedback("Feedback 3");

        ClassMoment aulaInvertida = new ClassMoment();
        aulaInvertida.setQuestions(List.of(q1, q2, q3));

        Grade grade = new Grade();
        grade.setStudentId(studentId);
        grade.setAulaInvertida(aulaInvertida);

        return grade;
    }

    @Test
    void findAll() {
        int initialSize = gradeService.findAll().size();

        Grade newGrade = buildGrade("student1@gmail.com");
        gradeService.save(newGrade);

        Assertions.assertEquals(initialSize + 1, gradeService.findAll().size());

        gradeService.deleteById(newGrade.getGradeId());
        Assertions.assertEquals(initialSize, gradeService.findAll().size());
    }

    @Test
    void findById() {
        Grade newGrade = buildGrade("student2@gmail.com");
        gradeService.save(newGrade);

        Grade foundGrade = gradeService.findById(newGrade.getGradeId()).orElse(null);

        Assertions.assertNotNull(foundGrade);
        Assertions.assertEquals(newGrade.getStudentId(), foundGrade.getStudentId());
        Assertions.assertNotNull(foundGrade.getAulaInvertida());
        Assertions.assertEquals(3, foundGrade.getAulaInvertida().getQuestions().size());

        gradeService.deleteById(newGrade.getGradeId());
    }

    @Test
    void save() {
        Grade newGrade = buildGrade("student3@gmail.com");
        gradeService.save(newGrade);

        Optional<Grade> savedGrade = gradeService.findById(newGrade.getGradeId());
        Assertions.assertTrue(savedGrade.isPresent());
        Assertions.assertEquals("student3@gmail.com", savedGrade.get().getStudentId());

        gradeService.deleteById(newGrade.getGradeId());
    }

    @Test
    void deleteById() {
        Grade newGrade = buildGrade("student4@gmail.com");
        gradeService.save(newGrade);

        int sizeBefore = gradeService.findAll().size();

        gradeService.deleteById(newGrade.getGradeId());

        Assertions.assertEquals(sizeBefore - 1, gradeService.findAll().size());
    }
}
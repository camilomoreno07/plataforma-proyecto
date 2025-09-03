package com.proyectogrado.plataforma.grade.Repository;

import com.proyectogrado.plataforma.grade.Model.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface GradeRepository extends MongoRepository<Grade, String> {
    Optional<Grade> findByStudentIdAndCourseId(String studentId, String courseId);
    void deleteByCourseId(String courseId);
}

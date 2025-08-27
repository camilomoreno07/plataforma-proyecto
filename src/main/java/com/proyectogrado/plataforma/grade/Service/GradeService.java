package com.proyectogrado.plataforma.grade.Service;

import com.mongodb.client.MongoIterable;
import com.proyectogrado.plataforma.grade.Model.Grade;
import com.proyectogrado.plataforma.grade.Repository.GradeRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GradeService {

    @Autowired
    private GradeRepository repository;

    public List<Grade> findAll() {
        return repository.findAll();
    }

    public Optional<Grade> findById(String id) {
        return repository.findById(id);
    }

    public Grade save(Grade grade) {
        return repository.save(grade);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public Optional<Grade> findByStudentIdAndCourseId(String studentId, String courseId) {
        return repository.findByStudentIdAndCourseId(studentId, courseId);
    }
}

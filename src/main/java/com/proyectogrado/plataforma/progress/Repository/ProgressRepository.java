package com.proyectogrado.plataforma.progress.Repository;

import com.proyectogrado.plataforma.progress.Model.Progress;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends MongoRepository<Progress, String>{
    boolean existsByCourseIdAndStudentEmail(String courseId, String studentEmail);
    Optional<Progress> findByCourseIdAndStudentEmail(String courseId, String studentEmail);
    List<Progress> findAllByCourseId(String courseId);

    void deleteByCourseId(String courseId);
}

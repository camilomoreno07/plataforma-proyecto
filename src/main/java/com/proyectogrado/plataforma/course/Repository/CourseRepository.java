package com.proyectogrado.plataforma.course.Repository;

import com.proyectogrado.plataforma.course.Model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CourseRepository extends MongoRepository<Course, String>, CourseAggregationQuery
{
    List<Course> findByProfessorIdsContaining(String username);
    List<Course> findByStudentIdsContaining(String username);
}

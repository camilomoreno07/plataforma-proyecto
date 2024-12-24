package com.proyectogrado.plataforma.course.Repository;

import com.proyectogrado.plataforma.course.Model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {

}

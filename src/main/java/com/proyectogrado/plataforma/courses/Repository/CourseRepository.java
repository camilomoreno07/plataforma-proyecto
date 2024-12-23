package com.proyectogrado.plataforma.courses.Repository;

import com.proyectogrado.plataforma.courses.Model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {

}

package com.proyectogrado.plataforma.grade.Repository;

import com.proyectogrado.plataforma.grade.Model.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GradeRepository extends MongoRepository<Grade, String> {
}

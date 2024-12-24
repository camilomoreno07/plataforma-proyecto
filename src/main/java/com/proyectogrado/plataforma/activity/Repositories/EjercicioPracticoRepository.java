package com.proyectogrado.plataforma.activity.Repositories;

import com.proyectogrado.plataforma.activity.Models.EjercicioPractico;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EjercicioPracticoRepository extends MongoRepository<EjercicioPractico, String> {}


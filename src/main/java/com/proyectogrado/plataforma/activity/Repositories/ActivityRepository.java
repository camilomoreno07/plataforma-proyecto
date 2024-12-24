package com.proyectogrado.plataforma.activity.Repositories;

import com.proyectogrado.plataforma.activity.Models.*;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActivityRepository extends MongoRepository<Activity, String> {}



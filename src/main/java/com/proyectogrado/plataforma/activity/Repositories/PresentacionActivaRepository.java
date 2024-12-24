package com.proyectogrado.plataforma.activity.Repositories;

import com.proyectogrado.plataforma.activity.Models.PresentacionActiva;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PresentacionActivaRepository extends MongoRepository<PresentacionActiva, String> {}

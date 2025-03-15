package com.proyectogrado.plataforma.media.entities;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaFileRepository extends MongoRepository<MediaFile,Integer>
{
    boolean existsByFilePath(String filePath);
    Optional<MediaFile> findByFilePath(String filePath);
}

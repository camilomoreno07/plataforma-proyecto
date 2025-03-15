package com.proyectogrado.plataforma.media.entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.*;

@Data
@Document(collection = "files")
public class MediaFile
{
    @Id
    String id;
    String filePath;

    public MediaFile(String filePath)
    {
        this.filePath = filePath;
    }
}

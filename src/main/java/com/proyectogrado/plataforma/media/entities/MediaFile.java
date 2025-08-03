package com.proyectogrado.plataforma.media.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

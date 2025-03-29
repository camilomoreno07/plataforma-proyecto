package com.proyectogrado.plataforma.media.services;

import com.proyectogrado.plataforma.media.entities.MediaFile;
import com.proyectogrado.plataforma.media.entities.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class MediaFileService
{
    @Autowired
    private MediaFileRepository mediaFileRepository;

    // Esto toma la carpeta Downloads del disco C por defecto
    private String UPLOAD_DIR = System.getProperty("user.home") + "\\Downloads\\pruebas\\";

    public String saveFile(MultipartFile file)
    {
        try
        {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs(); // Crear la carpeta si no existe

            // Preparar el archivo
            String filePath = UPLOAD_DIR + file.getOriginalFilename();
            File localFile = new File(filePath);

            // Agregar sufijo en caso de nombres duplicados
            for(int i = 1; localFile.exists(); i++)
            {
                filePath = filePath + " ("+i+")";
                localFile = new File(filePath);
            }

            // Guardar el archivo
            file.transferTo(localFile);

            mediaFileRepository.save(new MediaFile(filePath));

            return "File saved in Downloads: " + filePath;
        }
        catch (IOException e) {return "Error uploading file: " + e.getMessage();}
    }

    // HACER LUEGO
    // public void loadFile() {}
}

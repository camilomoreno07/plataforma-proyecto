package com.proyectogrado.plataforma.media.controllers;

import com.proyectogrado.plataforma.media.exceptions.InvalidExperienceException;
import com.proyectogrado.plataforma.media.services.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

@RestController
@RequestMapping("/media")
public class MediaFileController
{
    @Autowired
    private MediaFileService mediaFileService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try
        {
            String fileUrl = mediaFileService.uploadFile(file);
            return ResponseEntity.ok(Map.of("url", fileUrl));
        }
        catch (IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error uploading file."));
        }
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try
        {
            Resource resource = mediaFileService.getFile(filename);
            if (!resource.exists()) {return ResponseEntity.notFound().build();}

            // Detecta el tipo MIME (image/png, application/pdf, etc.)
            String contentType = java.nio.file.Files.probeContentType(Path.of(resource.getURI()));
            if (contentType == null)
            {
                contentType = "application/octet-stream"; // fallback
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(resource);

        } catch (Exception e) {
            System.out.println("Error al servir el archivo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @DeleteMapping("/files/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename)
    {
        try
        {
            String messageType = "message";
            String message = mediaFileService.deleteFile(filename);

            Function<Map<String,?>, ResponseEntity<?>> responseEntity;
            if(message.equals("Archivo eliminado exitosamente"))
            {
                responseEntity = ResponseEntity::ok;
            }
            else
            {
                messageType = "error";
                if(message.equals("No se pudo eliminar el archivo"))
                {
                    responseEntity = body -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
                }
                else // if(message.equals("No se pudo eliminar el archivo"))
                {
                    responseEntity = body -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
                }
            }
            return responseEntity.apply(Map.of(messageType, message));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error eliminando archivo: " + e.getMessage()));
        }
    }

    @PostMapping("/upload/experience")
    public ResponseEntity<?> uploadExperience(@RequestParam("file") MultipartFile file)
    {
        try
        {
            String message = mediaFileService.uploadExperience(file);
            if(!message.equals("Empty file."))
            {
                return ResponseEntity.ok(Map.of("url", message));
            }
            else
            {
                return ResponseEntity.badRequest().body(Map.of("error", message));
            }
        }
        catch (IOException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error uploading file."));
        }
        catch(InvalidExperienceException e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/experiences/{experience}")
    public ResponseEntity<?> deleteExperience(@PathVariable("experience") String experience)
    {
        try
        {
            String messageType = "message";
            String message = mediaFileService.deleteExperience(experience);

            Function<Map<String,?>, ResponseEntity<?>> responseEntity;
            if(message.equals("Experiencia eliminada exitosamente"))
            {
                responseEntity = ResponseEntity::ok;
            }
            else
            {
                messageType = "error";
                if(message.equals("Experiencia no encontrada"))
                {
                    responseEntity = body -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
                }
                else // if(message.equals("No se pudo eliminar la experiencia"))
                {
                    responseEntity = body -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
                }
            }
            return responseEntity.apply(Map.of(messageType, message));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error eliminando la experiencia: " + e.getMessage()));
        }
    }
}

package com.proyectogrado.plataforma.media.controllers;

import com.proyectogrado.plataforma.media.exceptions.InvalidExperienceException;
import com.proyectogrado.plataforma.media.services.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/media")
public class MediaFileController {

    @Autowired
    private MediaFileService mediaFileService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = file.getOriginalFilename();
            File localFile = new File(uploadDir + fileName);
            file.transferTo(localFile);

            String fileUrl = "/media/files/" + fileName;

            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error uploading file."));
        }
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), "uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Detecta el tipo MIME (image/png, application/pdf, etc.)
            String contentType = java.nio.file.Files.probeContentType(filePath);
            if (contentType == null) {
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
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(System.getProperty("user.dir"), "uploads").resolve(filename).normalize();
            File file = filePath.toFile();

            if (file.exists()) {
                if (file.delete()) {
                    return ResponseEntity.ok(Map.of("message", "Archivo eliminado exitosamente"));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "No se pudo eliminar el archivo"));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Archivo no encontrado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error eliminando archivo: " + e.getMessage()));
        }
    }

    @PostMapping("/upload/experience")
    public ResponseEntity<?> uploadExperience(@RequestParam("file") MultipartFile file)
    {
        if (file.isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("error", "Empty file."));
        }

        Path experiencesPath = Path.of(System.getProperty("user.dir"), "/uploads/experiences");
        try (ZipInputStream zis = new ZipInputStream(file.getInputStream()))
        {
            Map<String, Boolean> expectedStructure = new HashMap<>();
            expectedStructure.put("indexFile", false);
            expectedStructure.put("buildFolder", false);
            expectedStructure.put("templateDataFolder", false);

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null)
            {
                Path currentPath = validateZipEntry(experiencesPath, entry, expectedStructure);
                if (entry.isDirectory())
                {
                    Files.createDirectories(currentPath);
                }
                else
                {
                    Files.createDirectories(currentPath.getParent());
                    Files.copy(zis, currentPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }

            if(expectedStructure.values().stream().anyMatch(flag -> flag.equals(false)))
            {
                throw new InvalidExperienceException();
            }

            String expName = file.getOriginalFilename().split("\\.", 2)[0];
            String fileUrl = "/media/experiences/"+expName;
            return ResponseEntity.ok(Map.of("url", fileUrl));
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

    private Path validateZipEntry(Path targetDir, ZipEntry entry, Map<String, Boolean> expectedStructure) throws IOException
    {
        String name = entry.getName();
        Path resolvedPath = targetDir.resolve(name).normalize();

        if (name.endsWith("/index.html"))
        {
            expectedStructure.replace("indexFile", true);
        }
        else if (name.endsWith("/Build/"))
        {
            expectedStructure.replace("buildFolder", true);
        }
        else if (name.endsWith("/TemplateData/"))
        {
            expectedStructure.replace("templateDataFolder", true);
        }

        if (!resolvedPath.startsWith(targetDir))
        {
            throw new IOException("Invalid ZIP entry: " + entry.getName());
        }

        return resolvedPath;
    }
}

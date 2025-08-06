package com.proyectogrado.plataforma.media.services;

import com.proyectogrado.plataforma.media.entities.MediaFileRepository;
import com.proyectogrado.plataforma.media.exceptions.InvalidExperienceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class MediaFileService
{
    @Autowired
    private MediaFileRepository mediaFileRepository;

    public String uploadFile(MultipartFile file) throws IOException
    {
        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = file.getOriginalFilename();
        File localFile = new File(uploadDir + fileName);
        file.transferTo(localFile);

        return fileName;
    }

    public Resource getFile(String filename) throws MalformedURLException
    {
        Path filePath = Paths.get(System.getProperty("user.dir"), "uploads").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        return resource;
    }

    public String deleteFile(String filename)
    {
        Path filePath = Paths.get(System.getProperty("user.dir"), "uploads").resolve(filename).normalize();
        File file = filePath.toFile();

        String message;
        if (file.exists())
        {
            if (file.delete()) {message = "Archivo eliminado exitosamente";}
            else {message = "No se pudo eliminar el archivo";}
        }
        else {message = "Archivo no encontrado";}

        return message;
    }

    public String uploadExperience(MultipartFile file) throws IOException
    {
        if (file.isEmpty()) {return "Empty file.";}

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
            return fileUrl;
        }
    }

    public String deleteExperience(String experience)
    {
        Path directoryPath = Paths.get(System.getProperty("user.dir"), "/uploads/experiences").resolve(experience).normalize();
        File directory = directoryPath.toFile();

        String message;
        if (directory.exists() && directory.isDirectory())
        {
            if (deleteFileRecursively(directory)) {message = "Experiencia eliminada exitosamente";}
            else {message = "No se pudo eliminar la experiencia";}
        }
        else {message = "Experiencia no encontrada";}
        return message;
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

    private boolean deleteFileRecursively(File file)
    {
        File[] content = file.listFiles();
        if(content != null)
        {
            for (File item : content)
            {
                deleteFileRecursively(item);
            }
        }
        return file.delete();
    }
}

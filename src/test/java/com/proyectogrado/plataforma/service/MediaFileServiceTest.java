package com.proyectogrado.plataforma.service;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.media.exceptions.InvalidExperienceException;
import com.proyectogrado.plataforma.media.services.MediaFileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@SpringBootTest
@Import(EmbedMongoConfig.class)
class MediaFileServiceTest
{
    @Autowired
    private MediaFileService mediaFileService;

    @Test
    void uploadFile()
    {
        MultipartFile file = localFileToMultipartFile(
                "example-image.jpg",
                "example-image-2.jpg",
                "image/jpeg"
        );

        Assertions.assertNotNull(file);
        Assertions.assertDoesNotThrow(() -> mediaFileService.uploadFile(file));
    }

    @Test
    void getFile()
    {
        Resource resource;
        try
        {
            resource = mediaFileService.getFile("example-image-1.jpg");
        }
        catch (Exception e)
        {
            Assertions.fail("Se detecto URL malformada en el archivo a buscar");
            return;
        }
        Assertions.assertTrue(resource.exists());
    }

    @Test
    void deleteFile() throws IOException
    {
        MultipartFile file = localFileToMultipartFile(
                "example-image.jpg",
                "example-image-for-delete.jpg",
                "image/jpeg"
        );
        mediaFileService.uploadFile(file);

        Resource resource = mediaFileService.getFile("example-image-for-delete.jpg");
        Assertions.assertTrue(resource.exists());

        mediaFileService.deleteFile("example-image-for-delete.jpg");
        Assertions.assertFalse(resource.exists());
    }

    @Test
    void uploadValidExperience() throws IOException
    {
        MultipartFile file = localFileToMultipartFile(
                "ValidDemo1.zip",
                "ValidDemo1.zip",
                "application/zip"
        );

        mediaFileService.uploadExperience(file);

        Assertions.assertTrue(getExperience("ValidDemo1").exists());
    }

    @Test
    void uploadInvalidExperience()
    {
        MultipartFile file = localFileToMultipartFile(
                "InvalidDemo.zip",
                "InvalidDemo.zip",
                "application/zip"
        );

        Assertions.assertThrows(InvalidExperienceException.class, () -> mediaFileService.uploadExperience(file));
    }

    @Test
    void deleteExperience() throws IOException
    {
        MultipartFile file = localFileToMultipartFile(
                "ValidDemo2.zip",
                "ValidDemo2.zip",
                "application/zip"
        );

        mediaFileService.uploadExperience(file);

        Assertions.assertTrue(getExperience("ValidDemo2").exists());

        mediaFileService.deleteExperience("ValidDemo2");

        Assertions.assertFalse(getExperience("ValidDemo2").exists());
    }

    private MultipartFile localFileToMultipartFile(String localFileName, String multipartFileName, String contentType)
    {
        File localFile = Paths.get("src/main/resources/test-data", localFileName).toFile();

        MultipartFile file;
        try (InputStream input = new FileInputStream(localFile))
        {
            file = new MockMultipartFile(
                    "file",
                    multipartFileName,
                    contentType,
                    input
            );
            return file;
        }
        catch (Exception e) {return null;}
    }

    private File getExperience(String experience)
    {
        return Paths.get(System.getProperty("user.dir"), "/uploads/experiences").resolve(experience).normalize().toFile();
    }
}
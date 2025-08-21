package com.proyectogrado.plataforma.controller;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.auth.Auth.AuthService;
import com.proyectogrado.plataforma.auth.Auth.LoginRequest;
import com.proyectogrado.plataforma.course.Service.CourseService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(EmbedMongoConfig.class)
class MediaFileControllerTest
{
    private String TEACHER_TOKEN;

    @Autowired
    public MediaFileControllerTest(AuthService authService)
    {
        TEACHER_TOKEN = authService.login(new LoginRequest("teacher1@gmail.com", "1234")).getToken();
    }

    @BeforeAll
    static void beforeAll()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @Test
    void uploadFile()
    {
        File file = getTestFile("example-image.jpg");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .multiPart("file", file, "image/jpeg")
        .when()
            .post("/media/upload")
        .then()
            .statusCode(200)
            .body("url", notNullValue());
    }

    @Test
    void getFile()
    {
        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .get("/media/files/example-image-1.jpg")
        .then()
            .statusCode(200)
            .contentType(containsString("image"));
    }

    @Test
    void deleteFile()
    {
        File file = getTestFile("example-image.jpg");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .multiPart("file", file, "image/jpeg")
        .when()
            .post("/media/upload")
        .then()
            .statusCode(200);

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/media/files/example-image.jpg")
        .then()
            .statusCode(200)
            .body("message", containsString("Archivo eliminado"));

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .get("/media/files/example-image.jpg")
        .then()
            .statusCode(404);
    }

    @Test
    void uploadValidExperience()
    {
        File zip = getTestFile("ValidDemo1.zip");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .multiPart("file", zip, "application/zip")
        .when()
            .post("/media/upload/experience")
        .then()
            .statusCode(200)
            .body("url", notNullValue());

        File extracted = getExperience("ValidDemo1");
        Assertions.assertTrue(extracted.exists());
    }

    @Test
    void uploadInvalidExperience()
    {
        File zip = getTestFile("InvalidDemo.zip");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .multiPart("file", zip, "application/zip")
        .when()
            .post("/media/upload/experience")
        .then()
            .statusCode(400)
            .body("error", notNullValue());
    }

    @Test
    void deleteExperience()
    {
        File zip = getTestFile("ValidDemo2.zip");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .multiPart("file", zip, "application/zip")
        .when()
            .post("/media/upload/experience")
        .then()
            .statusCode(200);

        File exp = getExperience("ValidDemo2");
        Assertions.assertTrue(exp.exists());

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/media/experiences/ValidDemo2")
        .then()
            .statusCode(200)
            .body("message", containsString("eliminada"));

        Assertions.assertFalse(exp.exists());
    }

    private File getTestFile(String name)
    {
        try
        {
            return new ClassPathResource("test-data/" + name).getFile();
        }
        catch (IOException e)
        {
            Assertions.fail("Test file not found: " + name);
            return null;
        }
    }

    private File getExperience(String experience)
    {
        return Paths.get(System.getProperty("user.dir"), "uploads", "experiences")
                .resolve(experience).normalize().toFile();
    }
}
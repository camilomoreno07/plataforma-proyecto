package com.proyectogrado.plataforma.controller;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.activity.Models.Activity;
import com.proyectogrado.plataforma.auth.Auth.AuthService;
import com.proyectogrado.plataforma.auth.Auth.LoginRequest;
import com.proyectogrado.plataforma.course.Service.CourseService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(EmbedMongoConfig.class)
class ActivityControllerTest
{
    private String TEACHER_TOKEN;

    @Autowired
    public ActivityControllerTest(AuthService authService)
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
    void getAllActivities()
    {
        given()
                .header("Authorization", "Bearer " + TEACHER_TOKEN)
                .when()
                .get("/api/activities")
                .then()
                .statusCode(200);
    }

    @Test
    void getActivityById()
    {
        Activity activity = new Activity();
        activity.setTitulo("Actividad REST");
        activity.setResumen("Test get by ID");

        String id =
                given()
                    .header("Authorization", "Bearer " + TEACHER_TOKEN)
                    .contentType("application/json")
                    .body(activity)
                .when()
                    .post("/api/activities")
                .then()
                    .statusCode(200)
                    .extract()
                    .path("activityId");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .get("/api/activities/{id}", id)
        .then()
            .statusCode(200)
            .body("titulo", equalTo("Actividad REST"));

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/api/activities/{id}", id)
        .then()
            .statusCode(200);
    }

    @Test
    void createActivity()
    {
        Activity activity = new Activity();
        activity.setTitulo("Crear Actividad");
        activity.setResumen("Test de creación");

        String id =
                given()
                    .header("Authorization", "Bearer " + TEACHER_TOKEN)
                    .contentType("application/json")
                    .body(activity)
                .when()
                    .post("/api/activities")
                .then()
                    .statusCode(200)
                    .body("titulo", equalTo("Crear Actividad"))
                    .extract()
                    .path("activityId");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/api/activities/{id}", id)
        .then()
            .statusCode(200);
    }

    @Test
    void updateActivity()
    {
        Activity activity = new Activity();
        activity.setTitulo("Actualizar");
        activity.setResumen("Antes de update");

        String id =
                given()
                    .header("Authorization", "Bearer " + TEACHER_TOKEN)
                    .contentType("application/json")
                    .body(activity)
                .when()
                    .post("/api/activities")
                .then()
                    .statusCode(200)
                    .extract()
                    .path("activityId");

        // Modificar objeto
        activity.setTitulo("Actualizado");
        activity.setResumen("Después del update");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType("application/json")
            .body(activity)
        .when()
            .put("/api/activities/{id}", id)
        .then()
            .statusCode(200)
            .body("titulo", equalTo("Actualizado"));

        // Cleanup
        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/api/activities/{id}", id)
        .then()
            .statusCode(200);
    }

    @Test
    void deleteActivity()
    {
        Activity activity = new Activity();
        activity.setTitulo("Eliminar Actividad");
        activity.setResumen("Se eliminará");

        String id =
                given()
                    .header("Authorization", "Bearer " + TEACHER_TOKEN)
                    .contentType("application/json")
                    .body(activity)
                .when()
                    .post("/api/activities")
                .then()
                    .statusCode(200)
                    .extract()
                    .path("activityId");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/api/activities/{id}", id)
        .then()
            .statusCode(200);

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .get("/api/activities/{id}", id)
        .then()
            .statusCode(404);
    }
}
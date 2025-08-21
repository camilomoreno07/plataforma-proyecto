package com.proyectogrado.plataforma.controller;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.auth.Auth.AuthService;
import com.proyectogrado.plataforma.auth.Auth.LoginRequest;
import com.proyectogrado.plataforma.course.Service.CourseService;
import com.proyectogrado.plataforma.grade.Model.Grade;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(EmbedMongoConfig.class)
class GradeControllerTest {

    private String TEACHER_TOKEN;

    @Autowired
    public GradeControllerTest(AuthService authService) {
        TEACHER_TOKEN = authService.login(new LoginRequest("teacher1@gmail.com", "1234")).getToken();
    }

    @BeforeAll
    static void beforeAll()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @Test
    void getAllGrades() {
        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/grades")
        .then()
            .statusCode(200)
            .body("$", Matchers.notNullValue());
    }

    @Test
    void getGradeById() {
        Grade grade = new Grade();
        grade.setStudentId("student1@gmail.com");
        grade.setEvaluationType("Parcial");
        grade.setResult("3.9");

        Grade created = given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType(ContentType.JSON)
            .body(grade)
        .when()
            .post("/api/grades")
        .then()
            .statusCode(200)
            .extract()
            .as(Grade.class);

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType(ContentType.JSON)
        .when()
            .get("/api/grades/" + created.getGradeId())
        .then()
            .statusCode(200)
            .body("gradeId", Matchers.equalTo(created.getGradeId()))
            .body("result", Matchers.equalTo("3.9"));

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/api/grades/" + created.getGradeId())
        .then()
            .statusCode(200);
    }

    @Test
    void createGrade() {
        Grade grade = new Grade();
        grade.setStudentId("student2@gmail.com");
        grade.setEvaluationType("Final");
        grade.setResult("4.2");

        Grade created = given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType(ContentType.JSON)
            .body(grade)
        .when()
            .post("/api/grades")
        .then()
            .statusCode(200)
            .extract()
            .as(Grade.class);

        Assertions.assertEquals("4.2", created.getResult());

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/api/grades/" + created.getGradeId())
        .then()
            .statusCode(200);
    }

    @Test
    void updateGrade() {
        Grade grade = new Grade();
        grade.setStudentId("student3@gmail.com");
        grade.setEvaluationType("Quiz");
        grade.setResult("3.6");

        Grade created = given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType(ContentType.JSON)
            .body(grade)
        .when()
            .post("/api/grades")
        .then()
            .statusCode(200)
            .extract()
            .as(Grade.class);

        created.setResult("4.5");

        Grade updated = given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType(ContentType.JSON)
            .body(created)
        .when()
            .put("/api/grades/" + created.getGradeId())
        .then()
            .statusCode(200)
            .extract()
            .as(Grade.class);

        Assertions.assertEquals("4.5", updated.getResult());

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/api/grades/" + created.getGradeId())
        .then()
            .statusCode(200);
    }

    @Test
    void deleteGrade() {
        Grade grade = new Grade();
        grade.setStudentId("student4@gmail.com");
        grade.setEvaluationType("Taller");
        grade.setResult("3.3");

        Grade created = given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType(ContentType.JSON)
            .body(grade)
        .when()
            .post("/api/grades")
        .then()
            .statusCode(200)
            .extract()
            .as(Grade.class);

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/api/grades/" + created.getGradeId())
        .then()
            .statusCode(200);

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .get("/api/grades/" + created.getGradeId())
        .then()
            .statusCode(404);
    }
}
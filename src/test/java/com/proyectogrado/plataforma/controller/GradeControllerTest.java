package com.proyectogrado.plataforma.controller;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.auth.Auth.AuthService;
import com.proyectogrado.plataforma.auth.Auth.LoginRequest;
import com.proyectogrado.plataforma.grade.Model.ClassMoment;
import com.proyectogrado.plataforma.grade.Model.Grade;
import com.proyectogrado.plataforma.grade.Model.Question;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(EmbedMongoConfig.class)
class GradeControllerTest {
    
    private final String TEACHER_TOKEN;

    @Autowired
    public GradeControllerTest(AuthService authService) {
        TEACHER_TOKEN = authService.login(new LoginRequest("teacher1@gmail.com", "1234")).getToken();
    }

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    private Grade buildGrade(String studentId) {
        Question q1 = new Question();
        q1.setResponse("Answer 1");
        q1.setFeedback("Feedback 1");

        Question q2 = new Question();
        q2.setResponse("Answer 2");
        q2.setFeedback("Feedback 2");

        ClassMoment aulaVirtual = new ClassMoment();
        aulaVirtual.setQuestions(List.of(q1, q2));

        Grade grade = new Grade();
        grade.setStudentId(studentId);
        grade.setAulaVirtual(aulaVirtual);

        return grade;
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
        Grade grade = buildGrade("student1@gmail.com");

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
                .body("studentId", Matchers.equalTo("student1@gmail.com"))
                .body("aulaVirtual.questions.size()", Matchers.equalTo(2));

        given()
                .header("Authorization", "Bearer " + TEACHER_TOKEN)
                .when()
                .delete("/api/grades/" + created.getGradeId())
                .then()
                .statusCode(200);
    }

    @Test
    void createGrade() {
        Grade grade = buildGrade("student2@gmail.com");

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

        Assertions.assertEquals("student2@gmail.com", created.getStudentId());
        Assertions.assertNotNull(created.getAulaVirtual());
        Assertions.assertEquals(2, created.getAulaVirtual().getQuestions().size());

        given()
                .header("Authorization", "Bearer " + TEACHER_TOKEN)
                .when()
                .delete("/api/grades/" + created.getGradeId())
                .then()
                .statusCode(200);
    }

    @Test
    void updateGrade() {
        Grade grade = buildGrade("student3@gmail.com");

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

        // update one question's feedback
        created.getAulaVirtual().getQuestions().get(0).setFeedback("Updated Feedback");

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

        Assertions.assertEquals("Updated Feedback", updated.getAulaVirtual().getQuestions().get(0).getFeedback());

        given()
                .header("Authorization", "Bearer " + TEACHER_TOKEN)
                .when()
                .delete("/api/grades/" + created.getGradeId())
                .then()
                .statusCode(200);
    }

    @Test
    void deleteGrade() {
        Grade grade = buildGrade("student4@gmail.com");

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
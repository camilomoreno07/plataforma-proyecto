package com.proyectogrado.plataforma.controller;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.auth.Auth.AuthService;
import com.proyectogrado.plataforma.auth.Auth.LoginRequest;
import com.proyectogrado.plataforma.progress.Model.MomentProgress;
import com.proyectogrado.plataforma.progress.Model.Progress;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(EmbedMongoConfig.class)
class ProgressControllerTest
{

    private final String STUDENT_TOKEN;

    @Autowired
    public ProgressControllerTest(AuthService authService) {
        STUDENT_TOKEN = authService.login(new LoginRequest("student1@gmail.com", "1234")).getToken();
    }

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    private Progress buildProgress(String studentId) {
        Progress progress = new Progress();
        progress.setStudentId(studentId);
        progress.setCourseId("course-restassured");
        progress.setAulaInvertida(new MomentProgress(2));
        progress.setTallerHabilidad(new MomentProgress(2));
        progress.setActividadExperiencial(new MomentProgress(2));
        return progress;
    }

    @Test
    void testCreateProgress() {
        Progress progress = buildProgress("student1@test.com");

        String id = given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .contentType(ContentType.JSON)
                .body(progress)
                .when()
                .post("/api/progress")
                .then()
                .statusCode(200)
                .body("studentId", Matchers.equalTo("student1@test.com"))
                .body("courseId", Matchers.equalTo("course-restassured"))
                .extract()
                .path("id");

        // Clean up
        given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .delete("/api/progress/" + id)
                .then()
                .statusCode(204);
    }

    @Test
    void testGetProgressById() {
        Progress progress = buildProgress("student2@test.com");

        String id = given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .contentType(ContentType.JSON)
                .body(progress)
                .post("/api/progress")
                .then().statusCode(200)
                .extract().path("id");

        given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .get("/api/progress/" + id)
                .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(id))
                .body("studentId", Matchers.equalTo("student2@test.com"));

        // Clean up
        given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .delete("/api/progress/" + id)
                .then().statusCode(204);
    }

    @Test
    void testGetProgressByCourseAndStudent() {
        Progress progress = buildProgress("student3@test.com");

        String id = given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .contentType(ContentType.JSON)
                .body(progress)
                .post("/api/progress")
                .then().statusCode(200)
                .extract().path("id");

        given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .get("/api/progress/course/course-restassured/student/student3@test.com")
                .then()
                .statusCode(200)
                .body("id", Matchers.equalTo(id))
                .body("courseId", Matchers.equalTo("course-restassured"));

        // Clean up
        given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .delete("/api/progress/" + id)
                .then().statusCode(204);
    }

    @Test
    void testGetPercentageById() {
        Progress progress = buildProgress("student4@test.com");

        String id = given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .contentType(ContentType.JSON)
                .body(progress)
                .post("/api/progress")
                .then().statusCode(200)
                .extract().path("id");

        given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .get("/api/progress/" + id + "/percentage")
                .then()
                .statusCode(200)
                .body(Matchers.equalTo("0.0"));

        // Clean up
        given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .delete("/api/progress/" + id)
                .then().statusCode(204);
    }

    @Test
    void testDeleteProgress() {
        Progress progress = buildProgress("student5@test.com");

        String id = given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .contentType(ContentType.JSON)
                .body(progress)
                .post("/api/progress")
                .then().statusCode(200)
                .extract().path("id");

        // Delete
        given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .delete("/api/progress/" + id)
                .then().statusCode(204);

        // Verify deletion
        given()
                .header("Authorization", "Bearer " + STUDENT_TOKEN)
                .get("/api/progress/" + id)
                .then().statusCode(404);
    }
}

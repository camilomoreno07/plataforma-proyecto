package com.proyectogrado.plataforma.controller;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.auth.Auth.AuthService;
import com.proyectogrado.plataforma.auth.Auth.LoginRequest;
import com.proyectogrado.plataforma.course.Model.Course;
import com.proyectogrado.plataforma.course.Service.CourseService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(EmbedMongoConfig.class)
class CourseControllerTest
{
    private String STUDENT_TOKEN;
    private String TEACHER_TOKEN;
    private String ADMIN_TOKEN;

    private CourseService courseService;

    @Autowired
    public CourseControllerTest(AuthService authService, CourseService courseService)
    {
        this.courseService = courseService;

        STUDENT_TOKEN = authService.login(new LoginRequest("student1@gmail.com", "1234")).getToken();
        TEACHER_TOKEN = authService.login(new LoginRequest("teacher1@gmail.com", "1234")).getToken();
        ADMIN_TOKEN = authService.login(new LoginRequest("admin1@gmail.com", "1234")).getToken();
    }

    @BeforeAll
    static void beforeAll()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @Test
    void getAllCourses_asAdmin_shouldReturnAllCourses()
    {
        given()
            .header("Authorization", "Bearer " + ADMIN_TOKEN)
        .when()
            .get("/api/courses")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0));
    }

    @Test
    void getAllCourses_asRegularUser_shouldReturnOnlyCoursesWhereEnrolled()
    {
        given()
            .header("Authorization", "Bearer " + STUDENT_TOKEN)
        .when()
            .get("/api/courses")
        .then()
            .statusCode(200)
            .body("size()", equalTo(4));
    }

    @Test
    void getCourseById_asTeacher()
    {
        Course course = courseService.findAll().stream()
                .filter(c -> c.getProfessorIds().contains("teacher1@gmail.com"))
                .findFirst()
                .orElseThrow();

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .get("/api/courses/" + course.getCourseId())
        .then()
            .statusCode(200);
    }

    @Test
    void createCourse_asTeacher_shouldSucceed()
    {
        Course course = new Course();
        course.setCourseName("Hemorragia Postparto");
        course.setCourseDescription("Test desde controlador");
        course.setStudentIds(List.of("student1@gmail.com"));
        course.setProfessorIds(List.of("teacher1@gmail.com"));

        String courseId = given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType(ContentType.JSON)
            .body(course)
        .when()
            .post("/api/courses")
        .then()
            .statusCode(200)
            .body("courseName", equalTo("Hemorragia Postparto"))
            .extract()
            .path("courseId");

        courseService.deleteById(courseId);
    }

    @Test
    void updateCourse_asTeacher_shouldSucceed() {
        Course course = new Course();
        course.setCourseName("Original");
        course.setCourseDescription("Antes de update");
        courseService.save(course);

        course.setCourseName("Actualizado");

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
            .contentType(ContentType.JSON)
            .body(course)
        .when()
            .put("/api/courses/" + course.getCourseId())
        .then()
            .statusCode(200)
            .body("courseName", equalTo("Actualizado"));

        courseService.deleteById(course.getCourseId());
    }

    @Test
    void deleteCourse_asTeacher_shouldSucceed() {
        Course course = new Course();
        course.setCourseName("A borrar");
        courseService.save(course);

        given()
            .header("Authorization", "Bearer " + TEACHER_TOKEN)
        .when()
            .delete("/api/courses/" + course.getCourseId())
        .then()
            .statusCode(200);

        Assertions.assertTrue(courseService.findById(course.getCourseId()).isEmpty());
    }

    @Test
    void getCoursesAssociatedToStudent_asThatStudent_shouldSucceed()
    {
        given()
            .header("Authorization", "Bearer " + STUDENT_TOKEN)
        .when()
            .get("/api/courses/forStudent/student1@gmail.com")
        .then()
            .statusCode(200)
            .body("student", equalTo("The First Student"))
            .body("courses", not(empty()));
    }

    @Test
    void getCoursesAssociatedToStudent_asOtherUser_shouldFail()
    {
        given()
            .header("Authorization", "Bearer " + STUDENT_TOKEN)
            .pathParam("studentUsername", "student2@gmail.com")
        .when()
            .get("/api/courses/forStudent/{studentUsername}")
        .then()
            .statusCode(403);
    }

    @Test
    void allCourseEndpoints_shouldRequireAuthentication()
    {
        String courseId = UUID.randomUUID().toString();

        // ---

        given()
        .when()
            .get("/api/courses")
        .then()
            .statusCode(403);

        given()
            .pathParam("studentUsername", "student2@gmail.com")
        .when()
            .get("/api/courses/forStudent/{studentUsername}")
        .then()
        .statusCode(403);

        // ---

        given()
        .when()
            .delete("/api/courses/" + courseId)
        .then()
            .statusCode(403);

        // ---

        given()
            .body(Optional.empty())
        .when()
            .put("/api/courses/" + courseId)
        .then()
            .statusCode(403);
    }
}
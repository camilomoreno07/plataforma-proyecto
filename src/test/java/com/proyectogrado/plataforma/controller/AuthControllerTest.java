package com.proyectogrado.plataforma.controller;

import com.proyectogrado.plataforma.EmbedMongoConfig;
import com.proyectogrado.plataforma.auth.Auth.LoginRequest;
import com.proyectogrado.plataforma.auth.Auth.RegisterRequest;
import com.proyectogrado.plataforma.auth.Entities.Role;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Import(EmbedMongoConfig.class)
class AuthControllerTest
{
    @BeforeAll
    static void beforeAll()
    {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    @Test
    void loginSuccess()
    {
        LoginRequest loginRequest = new LoginRequest("student1@gmail.com", "1234");

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("token", not(emptyString()))
            .body("role", not(emptyString()))
            .body("message", emptyString());
    }

    @Test
    void loginFailure()
    {
        LoginRequest loginRequest = new LoginRequest("unknown@gmail.com", "unknown");

        given()
            .contentType(ContentType.JSON)
            .body(loginRequest)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401)
            .body("token", emptyString())
            .body("role", emptyString())
            .body("message", equalTo("Invalid username or password."));
    }

    @Test
    void registerSuccess()
    {
        RegisterRequest registerRequest = new RegisterRequest(
                "newcomer2@gmail.com", "1234", "New", "Comer", "Colombia", Role.STUDENT
        );

        given()
            .contentType(ContentType.JSON)
            .body(registerRequest)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(200)
            .body("token", not(emptyString()))
            .body("role", equalTo("STUDENT"))
            .body("message", equalTo(""));
    }

    @Test
    void registerDuplicateEmail()
    {
        RegisterRequest registerRequest = new RegisterRequest(
                "student1@gmail.com", "1234", "Dup", "User", "Colombia", Role.STUDENT
        );

        given()
            .contentType(ContentType.JSON)
            .body(registerRequest)
        .when()
            .post("/auth/register")
        .then()
            .statusCode(409)
            .body("token", equalTo(""))
            .body("message", equalTo("Email (username) already taken. Please choose a different one."));
    }
}
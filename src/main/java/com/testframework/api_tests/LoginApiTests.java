package com.testframework.api_tests;

import com.testframework.core.BaseApiTest;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*; // <-- ADD THIS LINE

public class LoginApiTests extends BaseApiTest {

    @Test(testName = "TC-API-07")
    public void loginSuccessful() {
        String requestBody = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"cityslicka\" }";
        given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    @Test(testName = "TC-API-08")
    public void loginUnsuccessful() {
        String requestBody = "{ \"email\": \"peter@klaven\" }";
        given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(400)
                .body("error", equalTo("Missing password"));
    }

    @Test(testName = "TC-API-09")
    public void listResources() {
        given()
                .when()
                .get("/unknown")
                .then()
                .statusCode(200)
                .body("data", hasSize(6));
    }

    @Test(testName = "TC-API-10")
    public void singleResourceNotFound() {
        given()
                .when()
                .get("/unknown/23")
                .then()
                .statusCode(200);
    }
}
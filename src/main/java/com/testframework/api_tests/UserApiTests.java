package com.testframework.api_tests;

import com.testframework.core.BaseApiTest;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*; // <-- ADD THIS LINE

public class UserApiTests extends BaseApiTest {

    @Test(testName = "TC-API-01")
    public void listUsers() {
        given()
                .queryParam("page", 2)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("page", equalTo(2))
                .body("data", hasSize(6));
    }

    @Test(testName = "TC-API-02")
    public void getSingleUser() {
        given()
                .when()
                .get("/users/2")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(2));
    }

    @Test(testName = "TC-API-03")
    public void getSingleUserNotFound() {
        given()
                .when()
                .get("/users/23")
                .then()
                .statusCode(404);
    }

    @Test(testName = "TC-API-04")
    public void createUser() {
        String requestBody = "{ \"name\": \"morpheus\", \"job\": \"leader\" }";
        given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body("name", equalTo("morpheus"));
    }

    @Test(testName = "TC-API-05")
    public void updateUserPut() {
        String requestBody = "{ \"name\": \"morpheus\", \"job\": \"zion resident\" }";
        given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .put("/users/2")
                .then()
                .statusCode(200)
                .body("job", equalTo("zion resident"));
    }

    @Test(testName = "TC-API-06")
    public void deleteUser() {
        given()
                .when()
                .delete("/users/2")
                .then()
                .statusCode(204);
    }
}
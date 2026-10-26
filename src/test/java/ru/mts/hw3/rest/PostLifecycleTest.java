package ru.mts.hw3.rest;

import org.junit.jupiter.api.Test;
import ru.mts.hw3.support.ApiSpecs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class PostLifecycleTest {

    @Test
    void patchedPostKeepsIdAndUnrelatedFieldWhileTitleChanges() {
        String originalBody = "Original body that PATCH must not touch";

        String postId = given()
                .spec(ApiSpecs.playgroundSpec())
                .body("""
                        {
                          "title": "Original title",
                          "body": "%s",
                          "user_id": 1
                        }
                        """.formatted(originalBody))
        .when()
                .post("/posts")
        .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("Original title"))
                .body("body", equalTo(originalBody))
                .extract().jsonPath().getString("id");

        given()
                .spec(ApiSpecs.playgroundSpec())
                .body("""
                        {
                          "title": "Updated title"
                        }
                        """)
        .when()
                .patch("/posts/{id}", postId)
        .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("title", equalTo("Updated title"));

        given()
                .spec(ApiSpecs.playgroundSpec())
        .when()
                .get("/posts/{id}", postId)
        .then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("title", equalTo("Updated title"))
                .body("body", equalTo(originalBody));
    }
}

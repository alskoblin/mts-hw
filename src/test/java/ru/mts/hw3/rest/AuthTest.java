package ru.mts.hw3.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.mts.hw3.support.ApiSpecs;
import ru.mts.hw3.support.Credentials;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class AuthTest {

    private static String loginPayload(String username, String password) {
        return """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);
    }

    @Test
    void loginIssuesAccessTokenThatUnlocksProtectedEndpoint() {
        String accessToken = given()
                .spec(ApiSpecs.playgroundSpec())
                .body(loginPayload(Credentials.username(), Credentials.password()))
        .when()
                .post("/auth/login")
        .then()
                .statusCode(200)
                .body("access_token", instanceOf(String.class))
                .body("access_token", not(emptyString()))
                .extract().path("access_token");

        given()
                .spec(ApiSpecs.playgroundSpec())
                .header("Authorization", "Bearer " + accessToken)
        .when()
                .get("/auth/me")
        .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("username", equalToIgnoringCase(Credentials.username()))
                .body("email", notNullValue());
    }

    @Test
    void protectedEndpointRejectsMissingToken() {
        given()
                .spec(ApiSpecs.playgroundSpec())
        .when()
                .get("/auth/me")
        .then()
                .statusCode(401);
    }

    @Test
    void protectedEndpointRejectsInvalidToken() {
        given()
                .spec(ApiSpecs.playgroundSpec())
                .header("Authorization", "Bearer clearly-invalid-token")
        .when()
                .get("/auth/me")
        .then()
                .statusCode(401);
    }

    static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                Arguments.of(Credentials.username(), "wrong-password", "неверный пароль"),
                Arguments.of("NoSuchUser", Credentials.password(), "несуществующий пользователь"),
                Arguments.of(Credentials.username(), Credentials.passwordWithFlippedCase(),
                        "пароль в неверном регистре")
        );
    }

    @ParameterizedTest(name = "{2} -> 401")
    @MethodSource("invalidCredentials")
    void loginRejectsInvalidCredentialsWithoutIssuingToken(String username, String password, String scenario) {
        given()
                .spec(ApiSpecs.playgroundSpec())
                .body(loginPayload(username, password))
        .when()
                .post("/auth/login")
        .then()
                .statusCode(401)
                .body("access_token", nullValue())
                .body("message", notNullValue());
    }
}

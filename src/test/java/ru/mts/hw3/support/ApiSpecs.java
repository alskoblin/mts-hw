package ru.mts.hw3.support;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.config.LogConfig.logConfig;

public final class ApiSpecs {

    public static final String PLAYGROUND_BASE_URI = "https://playground.nileslabs.com/api/v1";
    public static final String GRAPHQL_BASE_URI = "https://graphql.postman-echo.com";

    private static final RestAssuredConfig LOG_ON_FAILURE = RestAssured.config()
            .logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails());

    private ApiSpecs() {
    }

    public static RequestSpecification playgroundSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(PLAYGROUND_BASE_URI)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(LOG_ON_FAILURE)
                .build();
    }

    public static RequestSpecification graphQlSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(GRAPHQL_BASE_URI)
                .setBasePath("/graphql")
                .setContentType(ContentType.JSON)
                .setConfig(LOG_ON_FAILURE)
                .build();
    }
}

package ru.mts.hw3.graphql;

import org.junit.jupiter.api.Test;
import ru.mts.hw3.support.ApiSpecs;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

class GraphQlTest {

    @Test
    void queryReturnsGreetingWithoutErrors() {
        String query = """
                {
                  "query": "query Hello { hello(person: { name: \\"Ivan\\", age: 35 }) }"
                }
                """;

        given()
                .spec(ApiSpecs.graphQlSpec())
                .body(query)
        .when()
                .post()
        .then()
                .statusCode(200)
                .body("errors", nullValue())
                .body("data", notNullValue())
                .body("data.hello", equalTo("Hello Ivan"));
    }

    @Test
    void mutationCreatesPersonWithRequestedFields() {
        String mutation = """
                {
                  "query": "mutation CreatePerson { createPerson(person: { name: \\"Student\\", age: 20 }) { id name age } }"
                }
                """;

        given()
                .spec(ApiSpecs.graphQlSpec())
                .body(mutation)
        .when()
                .post()
        .then()
                .statusCode(200)
                .body("errors", nullValue())
                .body("data.createPerson", notNullValue())
                .body("data.createPerson.id", notNullValue())
                .body("data.createPerson.name", equalTo("Student"))
                .body("data.createPerson.age", equalTo(20));
    }

    @Test
    void queryWithUnknownFieldReturnsErrorsAndNoData() {
        String invalidQuery = """
                {
                  "query": "query { noSuchField }"
                }
                """;

        given()
                .spec(ApiSpecs.graphQlSpec())
                .body(invalidQuery)
        .when()
                .post()
        .then()
                .statusCode(200)
                .body("data", nullValue())
                .body("errors", notNullValue())
                .body("errors.size()", greaterThan(0))
                .body("errors[0].message", containsString("noSuchField"));
    }
}

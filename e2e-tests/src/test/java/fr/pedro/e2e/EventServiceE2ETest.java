package fr.pedro.e2e;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class EventServiceE2ETest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    void shouldReturnListOfEvents() {
        given()
            .when()
            .get("/api/events")
            .then()
            .statusCode(200)
            .body("$", not(empty()));
    }
}

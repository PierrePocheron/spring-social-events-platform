import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class UserServiceE2ETest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    void shouldReturnListOfUsers() {
        given()
            .when()
            .get("/api/users")
            .then()
            .statusCode(200)
            .body("$", not(empty()));
    }
}

package com.meemaw.resource.chore;


import com.meemaw.resource.v1.page.PageResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.meemaw.matcher.SameJSON.sameJson;
import static io.restassured.RestAssured.given;

@QuarkusTest
@Tag("integration")
public class ChoreResourceTest {

    @Test
    public void postPath_shouldThrowError_whenPathNotFound() {
        given()
                .when().post("/" + UUID.randomUUID())
                .then()
                .statusCode(404)
                .body(sameJson("{\"error\":{\"message\":\"Resource Not Found\",\"reason\":\"Not Found\"," +
                        "\"statusCode\":404}}"));
    }

    @Test
    public void getPath_shouldThrowError_whenUnsupportedMethod() {
        given()
                .when().get(PageResource.PATH)
                .then()
                .statusCode(405)
                .body(sameJson("{\"error\":{\"message\":\"Method Not Allowed\",\"reason\":\"Method Not Allowed\",\"statusCode\":405}}"));
    }
}

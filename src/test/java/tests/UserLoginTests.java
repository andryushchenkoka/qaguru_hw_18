package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import pojo.Auth;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static tags.Tags.*;

public class UserLoginTests extends BaseTest {

    String URL = "https://reqres.in/",
            LOGIN_ENDPOINT = "/api/login";

    @Test
    @Tags({@Tag(POSITIVE), @Tag(LOGIN), @Tag(POST_REQUEST)})
    public void loginTest() {
        Auth authData = new Auth();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("cityslicka");
        Response response = given()
                .baseUri(URL)
                .when().log().all()
                .contentType(JSON)
                .body(authData)
                .post(LOGIN_ENDPOINT)
                .then().log().all()
                .statusCode(200)
                .extract().response();
        String authToken = response.path("token").toString();
        System.out.println(authToken);
    }

    @Test
    @Tags({@Tag(NEGATIVE), @Tag(LOGIN), @Tag(POST_REQUEST)})
    public void loginNegativeTest() {
        Auth authData = new Auth();
        authData.setEmail("peter@klaven");
        given()
                .baseUri(URL)
                .when().log().all()
                .contentType(JSON)
                .body(authData)
                .post(LOGIN_ENDPOINT)
                .then().log().all()
                .statusCode(400);
    }
}

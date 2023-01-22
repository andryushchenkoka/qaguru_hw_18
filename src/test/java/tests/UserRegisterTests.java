package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import pojo.Auth;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.core.Is.is;
import static tags.Tags.*;

public class UserRegisterTests {

    String URL = "https://reqres.in/",
            REGISTER_ENDPOINT = "/api/register/";

    @Test
    @Tags({@Tag(POSITIVE), @Tag(REGISTER), @Tag(POST_REQUEST)})
    public void registerTest() {
        Auth authData = new Auth();
        authData.setEmail("eve.holt@reqres.in");
        authData.setPassword("pistol");
        Response response = given()
                .baseUri(URL)
                .contentType(JSON)
                .when().log().all()
                .body(authData)
                .post(REGISTER_ENDPOINT)
                .then().log().all()
                .statusCode(200)
                .extract().response();
        String token = response.path("token").toString();
        System.out.println(token);
    }

    @Test
    @Tags({@Tag(NEGATIVE), @Tag(REGISTER), @Tag(POST_REQUEST)})
    public void registerNegativeTest() {
        Auth authDataWrong = new Auth();
        authDataWrong.setEmail("sydney@fife");
        given()
                .baseUri(URL)
                .contentType(JSON)
                .when().log().all()
                .body(authDataWrong)
                .post(REGISTER_ENDPOINT)
                .then().log().all()
                .statusCode(400)
                .body("error", is("Missing password"));
    }
}

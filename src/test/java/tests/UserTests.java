package tests;

import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import pojo.User;
import pojo.UserWorker;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.core.Is.is;
import static tags.Tags.*;

public class UserTests {
    String URL = "https://reqres.in/",
            USERS_ENDPOINT = "api/users/";

    @ValueSource(ints = {
            1, 2
    })
    @ParameterizedTest
    @Tags({@Tag(POSITIVE), @Tag(GET_REQUEST)})
    public void getUsersListTest(int page) {
        step("Получить список пользователей на {0} странице", () -> {
            List<User> users = given()
                    .baseUri(URL)
                    .when().log().all()
                    .contentType(JSON)
                    .param("page", page)
                    .get(USERS_ENDPOINT)
                    .then().log().all()
                    .statusCode(200)
                    .extract().body().jsonPath().getList("data", User.class);

            Assertions.assertNotNull(users.size());
        });
    }

    @CsvSource(value = {
            "1, George",
            "6, Tracey",
            "7, Michael",
            "12, Rachel",
    })
    @ParameterizedTest
    @Tags({@Tag(POSITIVE), @Tag(GET_REQUEST)})
    public void getUserTest(int userId, String name) {
        step("Email содержит имя {0} пользователя - {1}", () -> {
            Response response = given()
                    .baseUri(URL)
                    .when().log().all()
                    .contentType(JSON)
                    .param("id", userId)
                    .get(USERS_ENDPOINT)
                    .then().log().all()
                    .statusCode(200)
                    .body("data.first_name", is(name))
                    .extract().response();

            Assertions.assertTrue(response.path("data.email").toString().contains(name.toLowerCase()));
        });
    }

    @Test
    @Tags({@Tag(NEGATIVE), @Tag(GET_REQUEST)})
    public void getUserNegativeTest() {
        step("Поиск пользователя с несуществующим id", () -> {
            Response response = given()
                    .baseUri(URL)
                    .when().log().all()
                    .contentType(JSON)
                    .param("id", 13)
                    .get(USERS_ENDPOINT)
                    .then().log().all()
                    .statusCode(404)
                    .extract().response();

            Assertions.assertEquals(response.path("").toString(), "{}");
        });
    }

    @Test
    @Tags({@Tag(POSITIVE), @Tag(POST_REQUEST)})
    public void createUserTest() {
        step("Создание нового пользователя", () -> {
            UserWorker userForCreate = new UserWorker();
            userForCreate.setName("Steve Jobs");
            userForCreate.setJob("Programmer");
            Response response = given()
                    .baseUri(URL)
                    .contentType(JSON)
                    .when().log().all()
                    .body(userForCreate)
                    .post(USERS_ENDPOINT)
                    .then().log().all()
                    .statusCode(201)
                    .body("name", is(userForCreate.getName()))
                    .body("job", is(userForCreate.getJob()))
                    .extract().response();
            String createdUserId = response.path("id").toString();
            System.out.println(createdUserId);
        });
    }

    @Test
    @Tags({@Tag(POSITIVE), @Tag(PUT_REQUEST)})
    public void updateUserTest() {
        step("Обновить данные пользователя", () -> {
            UserWorker userForUpdate = new UserWorker();
            userForUpdate.setName("Linus Torvalds");
            userForUpdate.setJob("God");
            Response response = given()
                    .baseUri(URL)
                    .contentType(JSON)
                    .when().log().all()
                    .body(userForUpdate)
                    .param("id", 1)
                    .put(USERS_ENDPOINT)
                    .then().log().all()
                    .statusCode(200)
                    .body("name", is(userForUpdate.getName()))
                    .body("job", is(userForUpdate.getJob()))
                    .extract().response();
            String updateTime = response.path("updatedAt").toString();
            System.out.println(updateTime);
        });
    }

    @Test
    @Tags({@Tag(POSITIVE), @Tag(DELETE_REQUEST)})
    public void deleteUserTest() {
        step("Удалить пользователя", () -> {
            given()
                    .baseUri(URL)
                    .when().log().all()
                    .param("id", 3)
                    .delete(USERS_ENDPOINT)
                    .then().log().all()
                    .statusCode(204);
        });
    }
}

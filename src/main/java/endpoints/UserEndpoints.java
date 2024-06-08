package endpoints;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payloads.User;

public class UserEndpoints {

    public Response createUser(User payload) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .log()
                .all()
                .when()
                .post(UserRoutes.postSingleUser)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response getUser(String userName) {
        return RestAssured
                .given()
                .pathParam("userName", userName)
                .log()
                .all()
                .when()
                .get(UserRoutes.getUserByName)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response updateUser(User payload, String userName) {
        return RestAssured
                .given()
                .pathParam("userName", userName)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .log()
                .all()
                .when()
                .put(UserRoutes.updateUser)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public Response deleteUser(String userName) {
        return RestAssured
                .given()
                .pathParam("userName", userName)
                .log()
                .all()
                .when()
                .delete(UserRoutes.deleteUser)
                .then()
                .log().all()
                .extract()
                .response();
    }

}

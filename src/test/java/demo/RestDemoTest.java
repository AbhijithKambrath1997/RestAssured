package demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

public class RestDemoTest {

    @Test
    public void getUser() {
        RestAssured
                .given()
                .when()
                .get("https://reqres.in/api/users?page=2")
                .then()
                .statusCode(200)
                .body("page", equalTo(2))
                .log()
                .all();
    }

    @Test
    public void postUser() {

        //Data creation using Hashmap
        HashMap<Object, Object> payload = new HashMap<>();
        payload.put("name", "Abhi");
        payload.put("job", "Tester");
        RestAssured
                .given()
                .contentType("application/json")
                .body(payload)
                .when()
                .post("https://reqres.in/api/api/users")
                .then()
                .statusCode(201)
                .log()
                .all();
    }

    @Test
    public void putUser() throws JsonProcessingException {

        //Data creation using JsonObject
        JSONObject object = new JSONObject();
        object.put("name", "Jith");
        object.put("job", "Developer");

        int id = RestAssured
                .given()
                .contentType("application/json")
                .body(object.toString())
                .when()
                .post("https://reqres.in/api/api/users")
                .jsonPath().getInt("id");

        //Data creation using POJO class
        PojoClass pojoClass = new PojoClass();
        pojoClass.setName("Jith");
        pojoClass.setJob("Devops");

        //POJO -> JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pojoClass);
        System.out.println(json);

        RestAssured
                .given()
                .contentType("application/json")
                .body(json)
                .when()
                .put("https://reqres.in/api/api/users/" + id)
                .then()
                .statusCode(200)
                .log()
                .all();
    }

    @Test
    public void deleteUser() throws FileNotFoundException {

        //Data creation from File
        File file = new File("src/test/resources/body.json");
        FileReader fileReader = new FileReader(file);
        JSONTokener jsonTokener = new JSONTokener(fileReader);
        JSONObject jsonObject = new JSONObject(jsonTokener);

        int id = RestAssured
                .given()
                .contentType("application/json")
                .body(jsonObject.toString())
                .when()
                .post("https://reqres.in/api/api/users")
                .jsonPath().getInt("id");

        RestAssured
                .given()
                .when()
                .delete("https://reqres.in/api/api/users/" + id)
                .then()
                .statusCode(204)
                .log()
                .all();
    }

    @Test
    public void pathAndQueryParams() {
        RestAssured
                .given()
                .pathParam("path1", "api")
                .pathParam("path2", "users")
                .queryParam("page", "2")
                .when()
                .get("https://reqres.in/{path1}/{path2}")
                .then()
                .statusCode(200)
                .body("page", equalTo(2))
                .log()
                .body();
    }

    @Test
    public void testCookies() {
        Response response = RestAssured
                .given()
                .when()
                .get("https://www.google.com/");
        Map<String, String> cookies = response.getCookies();
        Headers headers = response.getHeaders();

        //print key with values
        for (String cookie : cookies.keySet()) {
            System.out.println("Cookie Key   ==>" + cookie);
            System.out.println("Cookie Value ==>" + response.getCookie(cookie));
        }

        //print headers
        for (Header header : headers) {
            System.out.println("Header Name  ==>" + header.getName());
            System.out.println("Header Value ==>" + header.getValue());
        }
    }

    @Test
    public void jsonValidation() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("https://reqres.in/api/users?page=2");

        String jsonResponseBody = response.getBody().asPrettyString();
        System.out.println(jsonResponseBody);

        JSONAssert.assertEquals(String.valueOf(HttpStatus.SC_OK),
                String.valueOf(response.getStatusCode()), JSONCompareMode.STRICT);

        //response.toString() and response.getBody().asString() is same only
        JSONAssert.assertEquals(response.asPrettyString(), response.getBody().asPrettyString(), JSONCompareMode.STRICT);

        JsonPath jsonPath = new JsonPath(jsonResponseBody);
        JSONAssert.assertEquals("2", jsonPath.get("page").toString(), JSONCompareMode.STRICT);
        JSONAssert.assertEquals("6", jsonPath.get("per_page").toString(), JSONCompareMode.STRICT);
        JSONAssert.assertEquals("6",
                String.valueOf(jsonPath.getList("data").size()), JSONCompareMode.STRICT);
    }

    @Test
    public void xmlValidation() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
        Response response = RestAssured
                .given()
                .queryParam("status", "available")
                .header("Accept", "application/xml")
                .when()
                .get("/pet/findByStatus");

        String xmlResponseBody = response.getBody().asPrettyString();
        System.out.println(xmlResponseBody);

        XmlPath xmlPath = new XmlPath(xmlResponseBody);
        int size = xmlPath.getList("pets.Pet").size();

        for (int i = 0; i < size; i++) {
            String name = xmlPath.get("pets.Pet.name[" + i + "]");
            System.out.println(name);
        }
    }

    @Test
    public void fileUpload() {
        String filename = "SampleImage.png";
        File file = new File("src/test/resources/SampleImage.png");

        RestAssured
                .given()
                .multiPart("file", file)
                .contentType("multipart/form-data")
                .when()
                .post("https://petstore.swagger.io/v2/pet/123/uploadImage")
                .then()
                .body("message", equalTo(
                        "additionalMetadata: null\nFile uploaded to ./" + filename + ", 68297 bytes"))
                .statusCode(200);
    }

    @Test
    public void jsonSchemaValidation() {
        RestAssured
                .given()
                .when()
                .get("https://reqres.in/api/users?page=2")
                .then()
                .statusCode(200)
                .body("page", equalTo(2))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema.json"))
                .log()
                .all();
    }
}
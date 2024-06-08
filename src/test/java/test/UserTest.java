package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import endpoints.UserEndpoints;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.testng.Assert;
import org.testng.annotations.Test;
import payloads.User;
import templates.TestTemplate;

public class UserTest {

    private static final String UNKNOWN = "unknown";
    private static final String MESSAGE = "message";
    private static final String ERROR = "error";
    private static final String CODE = "code";
    private static final String TYPE = "type";

    UserEndpoints userEndpoints = new UserEndpoints();
    Faker faker = new Faker();
    TestTemplate template = TestTemplate.create();

    @Test(priority = 1)
    public void testCreateUser() {
        User user = buildUser();
        Response response = createUser(user);

        template.print("Validate User Creation");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
        verifyResponse(200, UNKNOWN, String.valueOf(user.getId()), response);
    }

    @Test(priority = 2)
    public void testReadUser() {
        User user = buildUser();
        createUser(user);

        template.print("Validate User Created");
        verifyUser(user);
    }

    @Test(priority = 3)
    public void testUpdateUser() {
        User user = buildUser();
        createUser(user);
        user.setEmail(faker.internet().safeEmailAddress());

        template.print("Update User");
        Response response = userEndpoints.updateUser(user, user.getUsername());

        template.print("Validate User Update");
        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
        verifyResponse(200, UNKNOWN, String.valueOf(user.getId()), response);

        template.print("Verify User Updated");
        verifyUser(user);
    }

    @Test(priority = 4)
    public void testDeleteUser() {
        User user = buildUser();
        createUser(user);

        template.print("Delete User");
        Response response = userEndpoints.deleteUser(user.getUsername());

        template.print("Delete User");
        verifyResponse(200, UNKNOWN, user.getUsername(), response);

        Response getResponse = userEndpoints.getUser(user.getUsername());
        Assert.assertEquals(getResponse.getStatusCode(), HttpStatus.SC_NOT_FOUND);
        verifyResponse(1, ERROR, "User not found", getResponse);
    }

    private User buildUser() {
        template.print("Build User");
        Name name = faker.name();
        return User.builder()
                .id(faker.idNumber().hashCode())
                .username(name.username())
                .firstName(name.firstName())
                .lastName(name.lastName())
                .email(faker.internet().safeEmailAddress())
                .password(faker.internet().password())
                .phone(faker.phoneNumber().cellPhone())
                .build();
    }

    private Response createUser(User user) {
        template.print("Create User");
        Response response = userEndpoints.createUser(user);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        return response;
    }

    private void verifyResponse(int code, String type, String message, Response response) {
        JSONObject object = new JSONObject();
        object.put(CODE, code);
        object.put(TYPE, type);
        object.put(MESSAGE, message);
        JSONAssert.assertEquals(object.toString(), response.asString(), JSONCompareMode.LENIENT);
    }

    private void verifyUser(User user) {
        Response response = userEndpoints.getUser(user.getUsername());
        Assert.assertEquals(response.getStatusCode(), HttpStatus.SC_OK);
        ObjectMapper objectMapper = new ObjectMapper();
        String expected;
        try {
            expected = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        JSONAssert.assertEquals(expected, response.asString(), JSONCompareMode.LENIENT);
    }
}

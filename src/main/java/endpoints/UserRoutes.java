package endpoints;

public class UserRoutes {

    public static String baseUrl = "https://petstore.swagger.io/v2";

    //User
    public static String postSingleUser = baseUrl + "/user";
    public static String getUserByName = baseUrl + "/user/{userName}";
    public static String updateUser = baseUrl + "/user/{userName}";
    public static String deleteUser = baseUrl + "/user/{userName}";
    public static String logInUserToSystem = baseUrl + "/user/login";
    public static String logOutUserToSystem = baseUrl + "/user/logout";
    public static String postMultipleUser = baseUrl + "/user/createWithList";

}

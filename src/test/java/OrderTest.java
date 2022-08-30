import client.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.apache.http.HttpStatus.*;

public class OrderTest {

    private String randomString = RandomStringUtils.randomAlphanumeric(5);
    private String password = RandomStringUtils.randomNumeric(4);
    private String name = RandomStringUtils.randomAlphabetic(6);

    private String[] mailCompanies = new String[]{"yandex", "mail", "rambler"};
    private int randomMailCompany = new Random().nextInt(mailCompanies.length);
    private String email = randomString + "@" + mailCompanies[randomMailCompany] + ".ru";

    private final static boolean EXPECTED_RESULT_TRUE = true;
    private final static boolean EXPECTED_RESULT_FALSE = false;
    private final static String[] EMPTY_INGREDIENTS = {};
    private final static String[] INGREDIENTS = {"61c0c5a71d1f82001bdaaa6d",
            "61c0c5a71d1f82001bdaaa6f", "61c0c5a71d1f82001bdaaa72"};
    private final static String[] INVALID_INGREDIENTS_HASH = {"123", "000", "777"};
    private final static String EMPTY_ACCESS_TOKEN = "";

    Response responseCreateUser;
    LoginResponse loginResponse;
    Response responseCreateOrder;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @After
    public void clear() {
        if (loginResponse != null) {
            Response responseDeleteCourier = User.sendDeleteRequestAuthUser(loginResponse.getAccessToken());
            Checks.checkExpectedResult(responseDeleteCourier, SC_ACCEPTED, EXPECTED_RESULT_TRUE);
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и с ингредиентами")
    public void createNewUserAndCreateOrder() {
        CreateUser user = User.createObjectUser(email, password, name);
        responseCreateUser = User.sendPostRequestAuthRegister(user);
        Checks.checkExpectedResult(responseCreateUser, SC_OK, EXPECTED_RESULT_TRUE);

        LoginUser loginObject = Login.createObjectLogin(email, password);
        Response responseLoginCourier = User.sendPostRequestAuthLogin(loginObject);
        loginResponse = Login.deserialization(responseLoginCourier);
        String accessToken = loginResponse.getAccessToken();

        CreateOrder order = Order.createObjectOrder(INGREDIENTS);
        responseCreateOrder = Order.sendPostRequestOrders(accessToken, order);
        Checks.checkExpectedResult(responseCreateOrder, SC_OK, EXPECTED_RESULT_TRUE);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, но без ингредиентов")
    public void createNewUserTryCreateOrderWithoutIngredients() {
        CreateUser user = User.createObjectUser(email, password, name);
        responseCreateUser = User.sendPostRequestAuthRegister(user);
        Checks.checkExpectedResult(responseCreateUser, SC_OK, EXPECTED_RESULT_TRUE);

        LoginUser loginObject = Login.createObjectLogin(email, password);
        Response responseLoginCourier = User.sendPostRequestAuthLogin(loginObject);
        loginResponse = Login.deserialization(responseLoginCourier);
        String accessToken = loginResponse.getAccessToken();

        CreateOrder order = Order.createObjectOrder(EMPTY_INGREDIENTS);
        responseCreateOrder = Order.sendPostRequestOrders(accessToken, order);
        Checks.checkExpectedResult(responseCreateOrder, SC_BAD_REQUEST, EXPECTED_RESULT_FALSE);
    }

    @Test
    @DisplayName("Попытка создания заказа без авторизации и без ингредиентов")
    public void tryCreateOrderWithoutAuthorizationAndIngredients() {
        CreateOrder order = Order.createObjectOrder(EMPTY_INGREDIENTS);
        responseCreateOrder = Order.sendPostRequestOrders(EMPTY_ACCESS_TOKEN, order);
        Checks.checkExpectedResult(responseCreateOrder, SC_BAD_REQUEST, EXPECTED_RESULT_FALSE);
    }

    @Test
    @DisplayName("Создание заказа без авторизации, но с ингредиентамит")
    public void tryCreateOrderWithoutAuthorizationWithIngredients() {
        CreateOrder order = Order.createObjectOrder(INGREDIENTS);
        responseCreateOrder = Order.sendPostRequestOrders(EMPTY_ACCESS_TOKEN, order);
        Checks.checkExpectedResult(responseCreateOrder, SC_OK, EXPECTED_RESULT_TRUE);
    }

    @Test
    @DisplayName("Попытка создания заказа с авторизацией и с неверным хешем ингредиентов")
    public void tryCreateOrderWithAuthorizationAndErrorIngredients() {
        CreateUser user = User.createObjectUser(email, password, name);
        responseCreateUser = User.sendPostRequestAuthRegister(user);
        Checks.checkExpectedResult(responseCreateUser, SC_OK, EXPECTED_RESULT_TRUE);

        LoginUser loginObject = Login.createObjectLogin(email, password);
        Response responseLoginCourier = User.sendPostRequestAuthLogin(loginObject);
        loginResponse = Login.deserialization(responseLoginCourier);
        String accessToken = loginResponse.getAccessToken();

        CreateOrder order = Order.createObjectOrder(INVALID_INGREDIENTS_HASH);
        responseCreateOrder = Order.sendPostRequestOrders(accessToken, order);
        Order.checkStatusCodeWithInvalidHash(responseCreateOrder, SC_INTERNAL_SERVER_ERROR);
    }
}
import client.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class LoginTest {

    private String password = RandomStringUtils.randomNumeric(4);

    private final static boolean EXPECTED_RESULT_TRUE = true;
    private final static boolean EXPECTED_RESULT_FALSE = false;
    private final static String INCORRECT_FIELDS = "email or password are incorrect";
    private final static String DEFAULT_EMAIL = "test-data@yandex.ru";
    private final static String DEFAULT_PASSWORD = "password";

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    public void successfulLogin() {
        LoginUser login = Login.createObjectLogin(DEFAULT_EMAIL, DEFAULT_PASSWORD);
        Response response = User.sendPostRequestAuthLogin(login);
        Checks.checkExpectedResult(response, SC_OK, EXPECTED_RESULT_TRUE);
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void loginWithWrongEmail() {
        LoginUser loginCourier = Login.createObjectLogin(DEFAULT_EMAIL, password);
        Response response = User.sendPostRequestAuthLogin(loginCourier);
        Checks.checkErrorMessage(response, SC_UNAUTHORIZED, EXPECTED_RESULT_FALSE, INCORRECT_FIELDS);
    }
}
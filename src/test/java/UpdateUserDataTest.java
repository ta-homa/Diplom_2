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

public class UpdateUserDataTest {

    private String randomString = RandomStringUtils.randomAlphanumeric(5);
    private String password = RandomStringUtils.randomNumeric(4);
    private String name = RandomStringUtils.randomAlphabetic(6);

    private String[] mailCompanies = new String[]{"yandex", "mail", "rambler"};
    private int randomMailCompany = new Random().nextInt(mailCompanies.length);
    private String email = randomString + "@" + mailCompanies[randomMailCompany] + ".ru";

    private final static boolean EXPECTED_RESULT_TRUE = true;
    private final static boolean EXPECTED_RESULT_FALSE = false;
    private final static String EMPTY_ACCESS_TOKEN = "";

    Response responseCreateUser;
    LoginResponse loginResponse;
    Response responseUpdateDataUser;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @After
    public void clear() {
        if (responseUpdateDataUser.statusCode() == SC_OK) {
            Response responseDeleteCourier = User.sendDeleteRequestAuthUser(loginResponse.getAccessToken());
            Checks.checkExpectedResult(responseDeleteCourier, SC_ACCEPTED, EXPECTED_RESULT_TRUE);
        }
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    public void createNewUserAndUpdateData() {
        CreateUser user = User.createObjectUser(email, password, name);
        responseCreateUser = User.sendPostRequestAuthRegister(user);
        Checks.checkExpectedResult(responseCreateUser, SC_OK, EXPECTED_RESULT_TRUE);

        LoginUser loginObject = Login.createObjectLogin(email, password);
        Response responseLoginCourier = User.sendPostRequestAuthLogin(loginObject);
        loginResponse = Login.deserialization(responseLoginCourier);
        String accessToken = loginResponse.getAccessToken();

        CreateUser newUser = User.createObjectUser(email, password, name);
        responseUpdateDataUser = User.sendPatchRequestAuthUser(accessToken, newUser);
        Checks.checkExpectedResultAfterUpdate(responseUpdateDataUser, SC_OK, EXPECTED_RESULT_TRUE);
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    public void updateUserDataWithoutLogin() {
        CreateUser newUser = User.createObjectUser(email, password, name);
        responseUpdateDataUser = User.sendPatchRequestAuthUser(EMPTY_ACCESS_TOKEN, newUser);
        Checks.checkExpectedResultAfterUpdate(responseUpdateDataUser, SC_UNAUTHORIZED, EXPECTED_RESULT_FALSE);
    }
}
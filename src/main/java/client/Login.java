package client;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.*;
public class Login {

    @Step("Создание объекта логин")
    public static LoginUser createObjectLogin(String email, String password) {
        return new LoginUser(email, password);
    }

    @Step("Десериализация ответа на логин пользователя")
    public static LoginResponse deserialization(Response responseLoginUser) {
        return responseLoginUser.as(LoginResponse.class);
    }
}
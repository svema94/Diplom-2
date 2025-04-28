import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import client.BurgerServiceClient;
import model.Credentials;
import model.TokenUser;
import model.User;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static constant.RandomData.*;
import static constant.Response.*;
import static constant.Request.*;


public class LoginUserTest {
    private User user;
    private BurgerServiceClient client;
    private ValidatableResponse responseLogin;
    private String token;

    @Before
    @DisplayName("Подготовка данных: создание пользователя")
    @Description("Создание пользователя перед каждым тестом. Предполагается, что пользователь успешно создан и получен его token.")
    public void createUser(){
        client = new BurgerServiceClient(BASE_URI);
        user = User.allField();
        ValidatableResponse responseCreate = client.createUser(user);
        Assume.assumeTrue(responseCreate.extract().statusCode() == SC_OK);
        token = responseCreate.extract().as(TokenUser.class).getToken();
    }

    @Test
    @DisplayName("Успешная авторизация пользователя")
    @Description("Проверка успешной авторизации пользователя с корректными полями email, password, name.")
    public void successAuthorization(){
        Credentials credentials = Credentials.fromUser(user);
        responseLogin = client.loginUser(credentials);
        responseLogin.assertThat().statusCode(SC_OK).body(ACCESS_TOKEN, notNullValue());
    }

    @Test
    @DisplayName("Авторизация без указания логина")
    @Description("Проверка, что система возвращает ошибку при попытке авторизации без указания поля email.")
    public void withoutEmail(){
        Credentials credentials = Credentials.withOtherEmail(user, EMPTY);
        responseLogin = client.loginUser(credentials);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED).body(MESSAGE, equalTo(AUTHORIZATION_USER_INCORRECT_FIELD));
    }

    @Test
    @DisplayName("Авторизация без указания пароля")
    @Description("Проверка, что система возвращает ошибку при попытке авторизации без указания поля password.")
    public void withoutPassword(){
        Credentials credentials = Credentials.withOtherPassword(user, EMPTY);
        responseLogin = client.loginUser(credentials);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED).body(MESSAGE, equalTo(AUTHORIZATION_USER_INCORRECT_FIELD));
    }

    @Test
    @DisplayName("Авторизация без указания имени")
    @Description("Проверка, что авторизация проходит успешно, даже если поле name не указано (оно не является обязательным для авторизации).")
    public void withoutName(){
        Credentials credentials = Credentials.withOtherName(user, EMPTY);
        responseLogin = client.loginUser(credentials);
        responseLogin.assertThat().statusCode(SC_OK).body(ACCESS_TOKEN, notNullValue());
    }

    @Test
    @DisplayName("Авторизация с некорректным логином")
    @Description("Проверка, что система возвращает ошибку при попытке авторизации с некорректным полем email.")
    public void withIncorrectEmail(){
        Credentials credentials = Credentials.withOtherEmail(user, EMAIL);
        responseLogin = client.loginUser(credentials);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED).body(MESSAGE, equalTo(AUTHORIZATION_USER_INCORRECT_FIELD));
    }

    @Test
    @DisplayName("Авторизация с некорректным паролем")
    @Description("Проверка, что система возвращает ошибку при попытке авторизации с некорректным полем password.")
    public void withIncorrectPassword(){
        Credentials credentials = Credentials.withOtherPassword(user, PASSWORD);
        responseLogin = client.loginUser(credentials);
        responseLogin.assertThat().statusCode(SC_UNAUTHORIZED).body(MESSAGE, equalTo(AUTHORIZATION_USER_INCORRECT_FIELD));
    }

    @Test
    @DisplayName("Авторизация с некорректным именем")
    @Description("Проверка, что авторизация проходит успешно, даже если указано некорректное имя (оно не влияет на авторизацию).")
    public void withIncorrectName(){
        Credentials credentials = Credentials.withOtherName(user, NAME);
        responseLogin = client.loginUser(credentials);
        responseLogin.assertThat().statusCode(SC_OK).body(SUCCESS, equalTo(true));
    }

    @After
    @DisplayName("Очистка данных: удаление пользователя")
    @Description("Удаление пользователя после выполнения каждого теста")
    public void dataCleaning(){
        ValidatableResponse responseDelete = client.deleteUser(token);
        responseDelete.assertThat().statusCode(SC_ACCEPTED).body(SUCCESS, equalTo(true));
    }
}
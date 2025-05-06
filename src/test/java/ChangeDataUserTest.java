import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.*;
import client.BurgerServiceClient;
import model.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static constant.RandomData.*;
import static constant.Response.*;
import static constant.Request.*;

public class ChangeDataUserTest {
    private User user;
    private BurgerServiceClient client;
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
    @DisplayName("Изменение логина авторизованного пользователя")
    @Description("Проверка успешного изменения поля email авторизованного пользователя.")
    public void authorizationAndChangeEmail(){
        Credentials credentials = Credentials.withOtherEmail(user, EMAIL);
        ValidatableResponse responseChange = client.changeDataUser(credentials, token);
        responseChange.assertThat().statusCode(SC_OK).body("user.email", equalTo(credentials.getEmail()));
    }

    @Test
    @DisplayName("Изменение имени авторизованного пользователя")
    @Description("Проверка успешного изменения поля name авторизованного пользователя.")
    public void authorizationAndChangeName(){
        Credentials credentials = Credentials.withOtherName(user, NAME);
        ValidatableResponse responseChange = client.changeDataUser(credentials, token);
        responseChange.assertThat().statusCode(SC_OK).body("user.name", equalTo(credentials.getName()));
    }

    @Test
    @DisplayName("Изменение логина без авторизации")
    @Description("Проверка, что система возвращает ошибку при попытке изменения поля email без авторизации.")
    public void withoutAuthorizationAndChangeEmail(){
        Credentials credentials = Credentials.withOtherEmail(user, EMAIL);
        ValidatableResponse responseChange = client.changeDataUser(credentials);
        responseChange.assertThat().statusCode(SC_UNAUTHORIZED).body(MESSAGE, equalTo(WITHOUT_AUTHORIZATION));
    }

    @Test
    @DisplayName("Изменение имени без авторизации")
    @Description("Проверка, что система возвращает ошибку при попытке изменения поля name без авторизации.")
    public void withoutAuthorizationAndChangeName(){
        Credentials credentials = Credentials.withOtherName(user, NAME);
        ValidatableResponse responseChange = client.changeDataUser(credentials);
        responseChange.assertThat().statusCode(SC_UNAUTHORIZED).body(MESSAGE, equalTo(WITHOUT_AUTHORIZATION));
    }

    @Test
    @DisplayName("Попытка изменения логина на уже существующий")
    @Description("Проверка, что система возвращает ошибку при попытке изменения поля email на уже существующий в системе.")
    public void authorizationAndRepeatEmail(){
        String existingEmail = user.getEmail();
        User userTwo = User.allField();
        ValidatableResponse responseCreateTwo = client.createUser(userTwo);
        responseCreateTwo.assertThat().statusCode(SC_OK);
        String tokenTwo = responseCreateTwo.extract().as(TokenUser.class).getToken();

        Credentials credentials = Credentials.withOtherEmail(userTwo, existingEmail);
        ValidatableResponse responseChange = client.changeDataUser(credentials, tokenTwo);
        responseChange.assertThat().statusCode(SC_FORBIDDEN).body(MESSAGE, equalTo(CHANGE_REPEAT_EMAIL));

        ValidatableResponse responseDelete = client.deleteUser(tokenTwo);
        responseDelete.assertThat().statusCode(SC_ACCEPTED).body(SUCCESS, equalTo(true));
    }

    @After
    @DisplayName("Очистка данных: удаление пользователя")
    @Description("Удаление пользователя после выполнения каждого теста")
    public void dataCleaning(){
        ValidatableResponse responseDelete = client.deleteUser(token);
        responseDelete.assertThat().statusCode(SC_ACCEPTED).body(SUCCESS, equalTo(true));

    }
}

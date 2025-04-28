import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assume;
import org.junit.Test;
import client.BurgerServiceClient;
import model.TokenUser;
import model.User;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static constant.Response.*;
import static constant.Request.*;

public class CreateUserTest {
    private User user;
    private BurgerServiceClient client;
    ValidatableResponse responseCreate;

    @Test
    @DisplayName("Создание нового пользователя с валидными данными")
    @Description("Проверка успешного создания пользователя, когда все обязательные поля (email, password, name) заполнены корректно.")
    public void createNewUser(){
        client = new BurgerServiceClient(BASE_URI);
        user = User.allField();
        responseCreate = client.createUser(user);
        responseCreate.assertThat().statusCode(SC_OK).body(SUCCESS, equalTo(true));
    }

    @Test
    @DisplayName("Создание пользователя без указания логина")
    @Description("Проверка, что система возвращает ошибку при попытке создания пользователя без указания поля email.")
    public void withoutEmail(){
        client = new BurgerServiceClient(BASE_URI);
        user = User.withoutEmail();
        responseCreate = client.createUser(user);
        responseCreate.assertThat().statusCode(SC_FORBIDDEN).body(MESSAGE, equalTo(CREATE_USER_EMPTY_FIELD));
    }

    @Test
    @DisplayName("Создание пользователя без указания пароля")
    @Description("Проверка, что система возвращает ошибку при попытке создания пользователя без указания поля password.")
    public void withoutPassword(){
        client = new BurgerServiceClient(BASE_URI);
        user = User.withoutPassword();
        responseCreate = client.createUser(user);
        responseCreate.assertThat().statusCode(SC_FORBIDDEN).body(MESSAGE, equalTo(CREATE_USER_EMPTY_FIELD));
    }

    @Test
    @DisplayName("Создание пользователя без указания имени")
    @Description("Проверка, что система возвращает ошибку при попытке создания пользователя без указания поля name.")
    public void withoutName(){
        client = new BurgerServiceClient(BASE_URI);
        user = User.withoutName();
        responseCreate = client.createUser(user);
        responseCreate.assertThat().statusCode(SC_FORBIDDEN).body(MESSAGE, equalTo(CREATE_USER_EMPTY_FIELD));
    }

    @Test
    @DisplayName("Создание пользователя с уже существующим логином")
    @Description("Проверка, что система возвращает ошибку при попытке создания пользователя с уже существующим полем email.")
    public void withExistingLogin(){
        client = new BurgerServiceClient(BASE_URI);
        user = User.allField();
        responseCreate = client.createUser(user);
        Assume.assumeTrue(responseCreate.extract().statusCode() == SC_OK);
        ValidatableResponse responseRepeat = client.createUser(user);
        responseRepeat.assertThat().statusCode(SC_FORBIDDEN).body(MESSAGE, equalTo(CREATE_USER_EXISTING_LOGIN));
    }

    @After
    @DisplayName("Очистка данных: удаление пользователя")
    @Description("Удаление пользователя после выполнения каждого теста, если токен был получен.")
    public void dataCleaning(){
        String token = responseCreate.extract().as(TokenUser.class).getToken();
        if (token != null){
            ValidatableResponse responseDelete = client.deleteUser(token);
            responseDelete.assertThat().statusCode(SC_ACCEPTED).body(SUCCESS, equalTo(true));
        }
    }
}

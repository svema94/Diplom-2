import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import client.BurgerServiceClient;
import model.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static constant.RandomData.*;
import static constant.Response.*;
import static constant.Request.*;


public class CreateOrderTest {
    private BurgerServiceClient client;
    private ValidatableResponse response;
    private Ingredients ingredients;
    private String token;

    @Before
    @DisplayName("Подготовка данных: создание пользователя")
    @Description("Создание пользователя перед каждым тестом. Предполагается, что пользователь успешно создан и получен его token.")
    public void createUser(){
        client = new BurgerServiceClient(BASE_URI);
        User user = User.allField();
        ValidatableResponse responseCreate = client.createUser(user);
        Assume.assumeTrue(responseCreate.extract().statusCode() == SC_OK);
        token = responseCreate.extract().as(TokenUser.class).getToken();
        String jsonResponse = client.receivingListIngredients().extract().asString();
        Ingredients.initHashIngredients(jsonResponse);
    }

    @Test
    @DisplayName("Создание заказа с одним ингредиентом")
    @Description("Проверка успешного создания заказа с одним ингредиентом и авторизованным пользователем.")
    public void oneIngredient(){
        ingredients = Ingredients.createListIngredients(1);
        response = client.createOrder(ingredients, token);
        response.assertThat().statusCode(SC_OK).body(ORDER, notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с несколькими ингредиентами")
    @Description("Проверка успешного создания заказа с несколькими ингредиентами и авторизованным пользователем.")
    public void multiIngredients(){
        ingredients = Ingredients.createListIngredients(12);
        response = client.createOrder(ingredients, token);
        response.assertThat().statusCode(SC_OK).body(ORDER, notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка, что система возвращает ошибку при попытке создания заказа без ингредиентов.")
    public void withoutIngredients(){
        ingredients = Ingredients.createListIngredients(0);
        response = client.createOrder(ingredients, token);
        response.assertThat().statusCode(SC_BAD_REQUEST).body(MESSAGE, equalTo(WITHOUT_INGREDIENTS));
    }

    @Test
    @DisplayName("Создание заказа с некорректным хэшем ингредиентов")
    @Description("Проверка, что система возвращает ошибку при попытке создания заказа с некорректным хэшем ингредиентов.")
    public void incorrectHashIngredients(){
        ingredients = Ingredients.createListIngredients(HASH_CODE);
        response = client.createOrder(ingredients, token);
        response.assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    @DisplayName("Очистка данных: удаление пользователя")
    @Description("Удаление пользователя после выполнения каждого теста")
    public void dataCleaning(){
        ValidatableResponse responseDelete = client.deleteUser(token);
        responseDelete.assertThat().statusCode(SC_ACCEPTED).body(SUCCESS, equalTo(true));
    }
}
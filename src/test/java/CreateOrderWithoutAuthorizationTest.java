import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import client.BurgerServiceClient;
import model.Ingredients;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static constant.RandomData.*;
import static constant.Response.*;
import static constant.Request.*;

public class CreateOrderWithoutAuthorizationTest {
    private BurgerServiceClient client;
    private Ingredients ingredients;
    private ValidatableResponse response;

    @Before
    @DisplayName("Подготовка данных: инициализация клиента")
    @Description("Инициализация клиента для работы с API перед каждым тестом.")
    public void createUser(){
        client = new BurgerServiceClient(BASE_URI);
        String jsonResponse = client.receivingListIngredients().extract().asString();
        Ingredients.initHashIngredients(jsonResponse);
    }

    @Test
    @DisplayName("Создание заказа с одним ингредиентом без авторизации")
    @Description("Проверка, что система возвращает ошибку при попытке создания заказа с одним ингредиентом без авторизации.")
    public void oneIngredient(){
        ingredients = Ingredients.createListIngredients(1);
        response = client.createOrder(ingredients);
        response.assertThat().statusCode(SC_OK);
    }

    @Test
    @DisplayName("Создание заказа с несколькими ингредиентами без авторизации")
    @Description("Проверка, что система возвращает ошибку при попытке создания заказа с несколькими ингредиентами без авторизации.")
    public void multiIngredients(){
        ingredients = Ingredients.createListIngredients(7);
        response = client.createOrder(ingredients);
        response.assertThat().statusCode(SC_OK);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов без авторизации")
    @Description("Проверка, что система возвращает ошибку при попытке создания заказа без ингредиентов без авторизации.")
    public void withoutIngredients(){
        ingredients = Ingredients.createListIngredients(0);
        response = client.createOrder(ingredients);
        response.assertThat().statusCode(SC_BAD_REQUEST).body(MESSAGE, equalTo(WITHOUT_INGREDIENTS));
    }

    @Test
    @DisplayName("Создание заказа с некорректным хэшем ингредиентов без авторизации")
    @Description("Проверка, что система возвращает ошибку при попытке создания заказа с некорректным хэшем ингредиентов без авторизации.")
    public void incorrectHashIngredients(){
        ingredients = Ingredients.createListIngredients(HASH_CODE);
        response = client.createOrder(ingredients);
        response.assertThat().statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}

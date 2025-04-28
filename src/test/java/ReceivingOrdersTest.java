import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import client.BurgerServiceClient;
import model.Ingredients;
import model.TokenUser;
import model.User;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static constant.Response.*;
import static constant.Request.*;

public class ReceivingOrdersTest {
    private BurgerServiceClient client;
    private String token;

    @Before
    @DisplayName("Подготовка данных: создание пользователя и заказа")
    @Description("Создание пользователя и заказа перед каждым тестом. Предполагается, что пользователь и заказ успешно созданы. Получен token пользователя")
    public void createUserAndOrder() {
        client = new BurgerServiceClient(BASE_URI);
        User user = User.allField();
        ValidatableResponse responseCreateUser = client.createUser(user);
        Assume.assumeTrue(responseCreateUser.extract().statusCode() == SC_OK);
        token = responseCreateUser.extract().as(TokenUser.class).getToken();

        String jsonResponse = client.receivingListIngredients().extract().asString();
        Ingredients.initHashIngredients(jsonResponse);

        Ingredients ingredients = Ingredients.createListIngredients(5);
        ValidatableResponse responseCreateOrder = client.createOrder(ingredients, token);
        Assume.assumeTrue(responseCreateOrder.extract().statusCode() == SC_OK);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Проверка успешного получения списка заказов для авторизованного пользователя.")
    public void receivingOrdersGivenUser(){
        ValidatableResponse response = client.receivingOrdersGivenUser(token);
        response.assertThat().statusCode(SC_OK).body(ORDERS, notNullValue());
    }

    @Test
    @DisplayName("Получение заказов без авторизации конкректного пользователя")
    @Description("Проверка, что система возвращает ошибку при попытке получения списка заказов без авторизации конкретного пользователя.")
    public void receivingOrdersWithAuthorization(){
        ValidatableResponse response = client.receivingOrdersGivenUser();
        response.assertThat().statusCode(SC_UNAUTHORIZED).body(MESSAGE, equalTo(WITHOUT_AUTHORIZATION));
    }

    @After
    @DisplayName("Очистка данных: удаление пользователя")
    @Description("Удаление пользователя после выполнения каждого теста")
    public void deleteUser(){
        ValidatableResponse responseDelete = client.deleteUser(token);
        responseDelete.assertThat().statusCode(SC_ACCEPTED).body(SUCCESS, equalTo(true));
    }
}

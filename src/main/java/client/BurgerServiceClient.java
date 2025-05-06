package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.Ingredients;
import model.User;
import static io.restassured.RestAssured.given;
import static constant.Request.*;

public class BurgerServiceClient {
    private final String baseURI;

    public BurgerServiceClient(String baseURI) {
        this.baseURI = baseURI;
    }

    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .body(user)
                .post(POST_CREATE_USER)
                .then()
                .log()
                .all();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(Credentials credentials){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .body(credentials)
                .post(POST_LOGIN_USER)
                .then()
                .log()
                .all();
    }

    @Step("Изменение данных пользователя с авторизацией")
    public ValidatableResponse changeDataUser(Credentials credentials, String token){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(credentials)
                .patch(ACTIONS_WITH_USER)
                .then()
                .log()
                .all();
    }

    @Step("Изменение данных пользователя без авторизации")
    public ValidatableResponse changeDataUser(Credentials credentials){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .body(credentials)
                .patch(ACTIONS_WITH_USER)
                .then()
                .log()
                .all();
    }

    @Step("Создание заказа с авторизацией")
    public ValidatableResponse createOrder(Ingredients ingredients, String token){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .header(HEADER_AUTHORIZATION, token)
                .body(ingredients)
                .post(ACTIONS_WITH_ORDER)
                .then()
                .log()
                .all();
    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrder(Ingredients ingredients){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .body(ingredients)
                .post(ACTIONS_WITH_ORDER)
                .then()
                .log()
                .all();
    }

    @Step("Получение заказа определенного пользователя с авторизацией в системе")
    public ValidatableResponse receivingOrdersGivenUser(String token){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .header(HEADER_AUTHORIZATION, token)
                .get(ACTIONS_WITH_ORDER)
                .then()
                .log()
                .all();
    }

    @Step("Получение заказа определенного пользователя без авторизации в системе")
    public ValidatableResponse receivingOrdersGivenUser(){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .get(ACTIONS_WITH_ORDER)
                .then()
                .log()
                .all();
    }

    @Step("Получение списка ингредиентов")
    public ValidatableResponse receivingListIngredients(){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .get(GET_LIST_INGREDIENTS)
                .then()
                .log()
                .all();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String token){
        return given()
                .log()
                .all()
                .baseUri(baseURI)
                .header(HEADER_CONTENT_TYPE, TYPE_JSON)
                .header(HEADER_AUTHORIZATION, token)
                .delete(ACTIONS_WITH_USER)
                .then()
                .log()
                .all();
    }
}

package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderApi {

    @Step("Создать заказ")
    public Response createOrder(Order order) {
        return given()
                .contentType("application/json")
                .body(order)
                .post("/api/v1/orders");
    }

    @Step("Получить список заказов")
    public Response getOrderList() {
        return given()
                .get("/api/v1/orders");
    }
}

package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;

public class OrderApi extends BaseApi {

    @Step("Создать заказ")
    public Response createOrder(Order order) {
        return requestSpec
                .body(order)
                .post("/api/v1/orders");
    }

    @Step("Получить список заказов")
    public Response getOrderList() {
        return requestSpec
                .get("/api/v1/orders");
    }
}

package order;

import io.qameta.allure.Step;
import org.junit.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrderListTest {

    @BeforeClass
    public static void setUpClass() {

        io.restassured.RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void testGetOrderList() {
        getOrderList()
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Получить список заказов")
    public io.restassured.response.Response getOrderList() {
        return given()
                .get("/api/v1/orders");
    }
}

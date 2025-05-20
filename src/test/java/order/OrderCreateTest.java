package order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreateTest {

    private final List<String> color;

    public OrderCreateTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColorOptions() {
        return new Object[][] {
                { Collections.singletonList("BLACK") },
                { Collections.singletonList("GREY") },
                { Arrays.asList("BLACK", "GREY") },
                { Collections.emptyList() }
        };
    }

    @BeforeClass
    public static void setUpClass() {
        io.restassured.RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void testCreateOrderWithDifferentColors() {
        // создание заказа с разными цветами
        createOrder(color)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Создать заказ с цветами {0}")
    public Response createOrder(List<String> color) {
        Map<String, Object> body = new HashMap<>();
        body.put("firstName", "Test");
        body.put("lastName", "User");
        body.put("address", "Москва, ул. Пушкина, 1");
        body.put("metroStation", 4);
        body.put("phone", "+7 999 888 77 66");
        body.put("rentTime", 3);
        body.put("deliveryDate", "2025-05-20");
        body.put("comment", "Тестовый заказ");
        body.put("color", color);

        return given()
                .contentType("application/json")
                .body(body)
                .when()
                .post("/api/v1/orders");
    }
}

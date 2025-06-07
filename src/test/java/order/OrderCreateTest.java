package order;

import io.restassured.response.Response;
import model.Order;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import steps.OrderApi;

import java.util.*;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderCreateTest {
    private final List<String> color;
    private OrderApi orderApi;

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

    @Before
    public void setUp() {
        orderApi = new OrderApi();
    }

    @Test
    public void testCreateOrderWithDifferentColors() {
        String[] color = {"BLACK", "GREY"};
        Order order = new Order(
                "Test", "User", "Москва, ул. Пушкина, 1", 4,
                "+7 999 888 77 66", 3, "2025-05-20",
                "Тестовый заказ", color);
        Response response = orderApi.createOrder(order);
        response.then().statusCode(201).body("track", notNullValue());
    }
}

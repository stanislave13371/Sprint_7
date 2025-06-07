package order;

import org.junit.*;
import steps.OrderApi;

import static org.hamcrest.Matchers.*;

public class OrderListTest {
    private OrderApi orderApi;

    @BeforeClass
    public static void setUpClass() {
        io.restassured.RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Before
    public void setUp() {
        orderApi = new OrderApi();
    }

    @Test
    public void testGetOrderList() {
        orderApi.getOrderList()
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}

package courier;

import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import org.junit.*;
import steps.CourierApi;

import static org.hamcrest.Matchers.*;

public class CourierCreateTest {
    private CourierApi courierApi;
    private Courier testCourier;
    private int courierId = 0;

    @BeforeClass
    public static void setUpClass() {
        io.restassured.RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        String login = "courier_" + System.currentTimeMillis();
        testCourier = new Courier(login, "1234", "Ivan");
    }

    @Test
    public void testCanCreateCourier() {
        Response response = courierApi.createCourier(testCourier);
        response.then().statusCode(201).body("ok", is(true));
        courierId = getCourierId();
    }

    @Test
    public void testCannotCreateDuplicateCourier() {
        courierApi.createCourier(testCourier);
        Response response = courierApi.createCourier(testCourier);
        response.then().statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
        courierId = getCourierId();
    }

    @Test
    public void testCannotCreateCourierWithoutPassword() {
        Courier courierNoPassword = new Courier(testCourier.getLogin(), null, testCourier.getFirstName());
        Response response = courierApi.createCourier(courierNoPassword);
        response.then().statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void testCannotCreateCourierWithoutLogin() {
        Courier courierNoLogin = new Courier(null, testCourier.getPassword(), testCourier.getFirstName());
        Response response = courierApi.createCourier(courierNoLogin);
        response.then().statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    // Вспомогательный метод для получения id курьера
    private int getCourierId() {
        CourierCredentials creds = new CourierCredentials(testCourier.getLogin(), testCourier.getPassword());
        Response response = courierApi.loginCourier(creds);
        return response.then().extract().path("id") != null ? response.path("id") : 0;
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierApi.deleteCourier(courierId);
        }
    }
}

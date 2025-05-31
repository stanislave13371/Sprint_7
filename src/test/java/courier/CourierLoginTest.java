package courier;

import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import org.junit.*;
import steps.CourierApi;

import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private CourierApi courierApi;
    private Courier testCourier;
    private int courierId;

    @BeforeClass
    public static void setUpClass() {
        io.restassured.RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Before
    public void setUp() {
        courierApi = new CourierApi();
        String login = "courier_" + System.currentTimeMillis();
        testCourier = new Courier(login, "1234", "Petr");
        courierApi.createCourier(testCourier);
    }

    @Test
    public void testCanLoginCourier() {
        CourierCredentials creds = new CourierCredentials(testCourier.getLogin(), testCourier.getPassword());
        Response response = courierApi.loginCourier(creds);
        response.then().statusCode(200)
                .body("id", notNullValue());
        courierId = response.path("id");
    }

    @Test
    public void testCannotLoginWithoutPassword() {
        CourierCredentials creds = new CourierCredentials(testCourier.getLogin(), null);
        Response response = courierApi.loginCourier(creds);
        response.then().statusCode(400)
                .body("message", containsString("Недостаточно данных для входа"));
    }

    @Test
    public void testLoginWithWrongPassword() {
        CourierCredentials creds = new CourierCredentials(testCourier.getLogin(), "wrong_password");
        Response response = courierApi.loginCourier(creds);
        response.then().statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    public void testLoginWithWrongLogin() {
        CourierCredentials creds = new CourierCredentials("not_exist_login", testCourier.getPassword());
        Response response = courierApi.loginCourier(creds);
        response.then().statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @After
    public void tearDown() {
        CourierCredentials creds = new CourierCredentials(testCourier.getLogin(), testCourier.getPassword());
        Response response = courierApi.loginCourier(creds);
        if (response.statusCode() == 200 && response.path("id") != null) {
            courierId = response.path("id");
            courierApi.deleteCourier(courierId);
        }
    }
}

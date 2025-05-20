package courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierLoginTest {
    private String login;
    private String password;
    private String firstName;
    private int courierId;

    @BeforeClass
    public static void setUpClass() {
        io.restassured.RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Before
    public void setUp() {
        login = "staas" + System.currentTimeMillis();
        password = "1111" + System.currentTimeMillis();
        firstName = "Stanislave";
        // Сначала создаём курьера
        createCourier(login, password, firstName);
    }

    @Test
    public void testCanLoginCourier() {
        // Позитивный кейс: логинимся с валидными данными
        loginCourier(login, password)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
        courierId = getCourierId(login, password);
    }

    @Test
    public void testCannotLoginWithoutRequiredField() {
        // нет пароля
        given()
                .contentType("application/json")
                .body("{ \"login\": \"" + login + "\" }")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400) // Должен возвращаться 400, но возвращается 504, возможно баг??
                .body("message", containsString("Недостаточно данных для входа"));
    }

    @Test
    public void testLoginWithWrongPassword() {
        // Логин с неправильным паролем
        loginCourier(login, "wrong_password")
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Test
    public void testLoginWithWrongLogin() {
        // Логин с несуществующим логином
        loginCourier("no_such_login", password)
                .then()
                .statusCode(404)
                .body("message", containsString("Учетная запись не найдена"));
    }

    @Step("Создать курьера")
    public void createCourier(String login, String password, String firstName) {
        given()
                .contentType("application/json")
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"" + firstName + "\" }")
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", is(true));
    }

    @Step("Логин курьера")
    public Response loginCourier(String login, String password) {
        return given()
                .contentType("application/json")
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }")
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Получить ID курьера")
    public int getCourierId(String login, String password) {
        Response response = loginCourier(login, password);
        return response.then().extract().path("id") != null ? response.path("id") : 0;
    }

    @Step("Удалить курьера по id")
    public void deleteCourier(int courierId) {
        given()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }

    @After
    public void tearDown() {
        // Получаем id для удаления
        courierId = getCourierId(login, password);
        if (courierId != 0) {
            deleteCourier(courierId);
        }
    }
}

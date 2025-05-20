package courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierCreateTest {
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
        password = "1111";
        firstName = "Stanislave";
        courierId = 0;
    }

    @Test
    public void testCanCreateCourier() {
        // Создаём курьера, проверяем статус и тело ответа
        createCourier(login, password, firstName)
                .then().statusCode(201)
                .body("ok", is(true));
        // После создания, залогинились
        courierId = getCourierId(login, password);
    }

    @Test
    public void testCannotCreateDuplicateCourier() {
        // Создаём курьера
        createCourier(login, password, firstName);
        // Пробуем создать такого же ещё раз - ждём 409 и сообщение об ошибке
        createCourier(login, password, firstName)
                .then().statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
        courierId = getCourierId(login, password); // чтобы удалить потом
    }

    @Test
    public void testCannotCreateWithoutRequiredField() {
        // Пробуем создать курьера без пароля
        given()
                .contentType("application/json")
                .body("{ \"login\": \"" + login + "\", \"firstName\": \"" + firstName + "\" }")
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .body("message", containsString("Недостаточно данных для создания учетной записи"));
    }

    @Test
    public void testCannotCreateWithExistingLogin() {
        // Создаём одного курьера
        createCourier(login, password, firstName);
        // Пробуем создать другого с тем же логином, но другим именем
        given()
                .contentType("application/json")
                .body("{ \"login\": \"" + login + "\", \"password\": \"2222\", \"firstName\": \"Petya\" }")
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .body("message", containsString("Этот логин уже используется"));
        courierId = getCourierId(login, password);
    }

    @Step("Создать курьера")
    public Response createCourier(String login, String password, String firstName) {
        // Шаг для Allure отчета
        return given()
                .contentType("application/json")
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\", \"firstName\": \"" + firstName + "\" }")
                .when()
                .post("/api/v1/courier");
    }

    @Step("Получить ID курьера")
    public int getCourierId(String login, String password) {
        // Для удаления после теста — логинимся и получаем id
        Response response = given()
                .contentType("application/json")
                .body("{ \"login\": \"" + login + "\", \"password\": \"" + password + "\" }")
                .when()
                .post("/api/v1/courier/login");
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
        // Чистим данные — только если курьер был создан
        if (courierId != 0) {
            deleteCourier(courierId);
        }
    }
}

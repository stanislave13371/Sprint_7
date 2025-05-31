package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierApi {

    @Step("Создать курьера")
    public Response createCourier(Courier courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .post("/api/v1/courier");
    }

    @Step("Логин курьера")
    public Response loginCourier(CourierCredentials creds) {
        return given()
                .contentType("application/json")
                .body(creds)
                .post("/api/v1/courier/login");
    }

    @Step("Удалить курьера по id={id}")
    public Response deleteCourier(int id) {
        return given()
                .delete("/api/v1/courier/" + id);
    }
}

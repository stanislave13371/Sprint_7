package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;

public class CourierApi extends BaseApi {

    @Step("Создать курьера")
    public Response createCourier(Courier courier) {
        return requestSpec
                .body(courier)
                .post("/api/v1/courier");
    }

    @Step("Логин курьера")
    public Response loginCourier(CourierCredentials creds) {
        return requestSpec
                .body(creds)
                .post("/api/v1/courier/login");
    }

    @Step("Удалить курьера по id={id}")
    public Response deleteCourier(int id) {
        return requestSpec
                .delete("/api/v1/courier/" + id);
    }
}

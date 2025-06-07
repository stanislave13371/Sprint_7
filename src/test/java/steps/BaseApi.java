package steps;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BaseApi {

    protected final RequestSpecification requestSpec;

    public BaseApi() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        requestSpec = RestAssured.given().contentType("application/json");
    }
}

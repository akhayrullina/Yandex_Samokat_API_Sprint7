package utils.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Data;
import pojo.Courier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Data
@AllArgsConstructor
public class CourierApi {
    private final RequestSpecification spec;

    @Step("Создание курьера")
    public Response createCourier(Courier courier) {
        return given()
                .spec(spec)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Авторизация курьера")
    public Response loginCourier(Courier courier) {
        return given()
                .spec(spec)
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Удаление курьера по id")
    public Response deleteCourier(int courierId) {
        return given()
                .spec(spec)
                .when()
                .delete("/api/v1/courier/" + courierId);
    }

    @Step("Удаление курьера по учётным данным")
    public Response deleteCourierByCredentials(Courier courier) {
        Response loginResponse = loginCourier(courier);
        int courierId = loginResponse.jsonPath().getInt("id");
        return deleteCourier(courierId);
    }

    @Step("Создание курьера с валидными данными")
    public static void createCourier(CourierApi courierApi, Courier courier) {
        Response response = courierApi.createCourier(courier);

        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Step("Удаление курьера после выполнения автотеста")
    public static void deleteCourierAfterTest(CourierApi courierApi, Courier courier) {
        courierApi.deleteCourierByCredentials(courier).then().statusCode(200).body("ok", equalTo(true));
    }
}

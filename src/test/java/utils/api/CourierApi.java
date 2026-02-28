package utils.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Data;
import pojo.Courier;

import static io.restassured.RestAssured.given;

@Data
@AllArgsConstructor
public class CourierApi {
    private final RequestSpecification spec;

    public Response createCourier(Courier courier) {
        return given()
                .spec(spec)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    public Response loginCourier(Courier courier) {
        return given()
                .spec(spec)
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    public Response deleteCourier(int courierId) {
        return given()
                .spec(spec)
                .when()
                .delete("/api/v1/courier/" + courierId);
    }
}

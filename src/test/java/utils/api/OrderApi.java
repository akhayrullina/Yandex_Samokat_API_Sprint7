package utils.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.AllArgsConstructor;
import lombok.Data;
import pojo.Order;
import static io.restassured.RestAssured.given;

@Data
@AllArgsConstructor
public class OrderApi {
    private final RequestSpecification spec;

    public Response getOrders() {
        return given()
                .spec(spec)
                .get("/api/v1/orders");
    }

    public Response createOrder(Order order) {
        return given()
                .spec(spec)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    public Response cancelOrder(String order) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .put("/api/v1/orders/cancel");
    }
}

package orders;

import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;

public class ListOfOrders {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";

        // Настраиваем RestAssured на игнорирование SSL ошибок
        RestAssured.config = RestAssured.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation());
    }

    @Test
    @DisplayName("Проверка получения списка заказов")
    public void getListOfOrders() {
        Response response =
                given()
                        .get("/api/v1/orders");
        response.then()
                .statusCode(200);
        System.out.println(response.body().asString());
    }
}

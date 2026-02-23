package orders;

import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreateOrderTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";

        // Настраиваем RestAssured на игнорирование SSL ошибок
        RestAssured.config = RestAssured.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation());
    }

    static List<Arguments> orderDetails() {
        return List.of(
                Arguments.of("{\"firstName\": \"Вася\", " +
                        "\"lastName\": \"Иванов\", " +
                        "\"address\": \"г. Москва, ул. Нестерова, д. 142, кв. 7\",\n" +
                        "\"metroStation\": 4,\n" +
                        "\"phone\": \"+7 917 355 35 35\",\n" +
                        "\"rentTime\": 5,\n" +
                        "\"deliveryDate\": \"2026-06-06\",\n" +
                        "\"comment\": \"Привезите рабочий самокат\",\n" +
                        "\"color\": [\"BLACK\"]}"),
                Arguments.of("{\"firstName\": \"Мария\", " +
                        "\"lastName\": \"Митрошина\", " +
                        "\"address\": \"г. Москва, ул. Садовая, д. 19, кв. 56\",\n" +
                        "\"metroStation\": 6,\n" +
                        "\"phone\": \"+7 917 555 35 35\",\n" +
                        "\"rentTime\": 5,\n" +
                        "\"deliveryDate\": \"2026-07-06\",\n" +
                        "\"comment\": \"Самокат любого цвета\",\n" +
                        "\"color\": [\"BLACK\", \"GREY\"]}"),
                Arguments.of("{\"firstName\": \"Анастасия\", " +
                        "\"lastName\": \"Горшкова\", " +
                        "\"address\": \"г. Москва, ул. Номостовая, д. 87, кв. 98\",\n" +
                        "\"metroStation\": 8,\n" +
                        "\"phone\": \"+7 917 888 35 35\",\n" +
                        "\"rentTime\": 5,\n" +
                        "\"deliveryDate\": \"2026-07-26\",\n" +
                        "\"comment\": \"Самокат любого цвета\",\n" +
                        "\"color\": []}")
        );
    }

    private ArrayList<Integer> ordersTrack = new ArrayList<>();

    @ParameterizedTest
    @MethodSource("orderDetails")
    @DisplayName("Проверка создания заказа с валидными данными")
    @Order(1)
    public void createOrderWithValidDataReturnsOk(String json) {
        Response orderResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/orders");
        orderResponse.then().assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);

        int orderTrack = orderResponse.jsonPath().getInt("track");
        ordersTrack.add(orderTrack);
    }


    @Test
    @DisplayName("Удаление заказов после выполнения автотестов")
    @Order(2)
    public void deleteOrderAfterTest() {
        for (int i = 0; i < ordersTrack.size(); i++) {
            String json = "{\"track\": " + ordersTrack.get(i) + "}";
            Response deleteResponse = given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(json)
                    .when()
                    .put("/api/v1/orders/cancel");
            deleteResponse.then().statusCode(200).body("ok", equalTo(true));
        }
    }
}

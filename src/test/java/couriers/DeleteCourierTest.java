package couriers;

import io.restassured.RestAssured;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteCourierTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";

        // Настраиваем RestAssured на игнорирование SSL ошибок
        RestAssured.config = RestAssured.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation());
    }

    static List<Arguments> courierDetailsForCreate() {
        return List.of(
                Arguments.of("{\"login\": \"courierAutotest1\", \"password\": \"qwerty123\", \"firstName\": \"DimaCourier\"}"),
                Arguments.of("{\"login\": \"courierAutotest3\", \"password\": \"qwerty456\", \"firstName\": \"VasyaCourier\"}")
        );
    }

    static List<Arguments> correctCourierDetails() {
        return List.of(
                Arguments.of("{\"login\": \"courierAutotest1\", \"password\": \"qwerty123\"}"),
                Arguments.of("{\"login\": \"courierAutotest3\", \"password\": \"qwerty456\"}")
        );
    }

    @ParameterizedTest
    @MethodSource("courierDetailsForCreate")
    @DisplayName("Создание курьера для проверки последующего удаления курьера")
    @Order(1)
    public void createCourierWithValidDataReturnsOk(String json) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    @DisplayName("Удаление курьера без корректного id")
    @Order(2)
    public void deleteCourierWithoutIdReturns400() {
        Response deleteResponse = given()
                .when()
                .delete("/api/v1/courier/");
        deleteResponse.then().statusCode(400).body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Удаление курьера с несуществующим id")
    @Order(3)
    public void deleteCourierWithNonExistentIdReturns404() {
        Response deleteResponse = given()
                .when()
                .delete("/api/v1/courier/" + 000000);
        deleteResponse.then().statusCode(404).body("message", equalTo("Курьера с таким id нет."));
    }

    @ParameterizedTest
    @MethodSource("correctCourierDetails")
    @DisplayName("Успешное удаление курьеров")
    @Order(4)
    public void deleteCourierDetailsReturns200(String json) {
        //Логин курьера, чтобы извлечь его id
        Response loginResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier/login");
        loginResponse.then().statusCode(200);

        //Извлечение id курьера
        int courierId = loginResponse.jsonPath().getInt("id");

        //Удаление курьера, используя id
        Response deleteResponse = given()
                .when()
                .delete("/api/v1/courier/" + courierId);
        deleteResponse.then().statusCode(200).body("ok", equalTo(true));
    }
}

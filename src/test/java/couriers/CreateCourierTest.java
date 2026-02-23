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
public class CreateCourierTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";

        // Настраиваем RestAssured на игнорирование SSL ошибок
        RestAssured.config = RestAssured.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation());
    }

    static List<Arguments> correctCourierDetails() {
        return List.of(
                Arguments.of("{\"login\": \"courierAutotest1\", \"password\": \"qwerty123\", \"firstName\": \"DimaCourier\"}"),
                Arguments.of("{\"login\": \"courierAutotest3\", \"password\": \"qwerty456\", \"firstName\": \"VasyaCourier\"}")
        );
    }

    static List<Arguments> incorrectCourierDetails() {
        return List.of(
                Arguments.of("{\"login\": \"\", \"password\": \"qwerty2323\", \"firstName\": \"PetyaCourier\"}"),
                Arguments.of("{\"login\": \"courierAutotest4\", \"password\": \"\", \"firstName\": \"IgorCourier\"}"),
                Arguments.of("{\"login\": \"courierAutotest5\", \"password\": \"qwerty555\", \"firstName\": \"\"}")
        );
    }

    static List<Arguments> courierDetailsForDeletion() {
        return List.of(
                Arguments.of("{\"login\": \"courierAutotest1\", \"password\": \"qwerty123\"}"),
                Arguments.of("{\"login\": \"courierAutotest3\", \"password\": \"qwerty456\"}"),
                Arguments.of("{\"login\": \"courierAutotest5\", \"password\": \"qwerty555\"}")
        );
    }

    @ParameterizedTest
    @MethodSource("correctCourierDetails")
    @DisplayName("Проверка создания курьера с валидными данными")
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

    @ParameterizedTest
    @MethodSource("correctCourierDetails")
    @DisplayName("Проверка создания двух одинаковых курьеров")
    @Order(2)
    public void createIdenticalCouriersReturns409(String json) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
    }

    @ParameterizedTest
    @MethodSource("incorrectCourierDetails")
    @DisplayName("Создание курьера, если одного из обязательных полей нет ")
    @Order(3)
    public void createCourierWithoutARequiredFieldReturns400(String json) {
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier");
        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @ParameterizedTest
    @MethodSource("courierDetailsForDeletion")
    @DisplayName("Удаление курьеров после выполнения автотестов")
    @Order(4)
    public void deleteCourierDetailsAfterTest(String json) {
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

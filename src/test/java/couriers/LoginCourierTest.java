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
import static org.hamcrest.Matchers.notNullValue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginCourierTest {

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

    static List<Arguments> incorrectCourierDetails() {
        return List.of(
                Arguments.of("{\"login\": \"courierAutotest1\", \"password\": \"\"}"),
                Arguments.of("{\"login\": \"\", \"password\": \"qwerty456\"}")
        );
    }

    @ParameterizedTest
    @MethodSource("courierDetailsForCreate")
    @DisplayName("Создание курьера для проверки авторизации")
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
    @DisplayName("Авторизация курьера в системе с валидным логином и паролем")
    @Order(2)
    public void loginCourierWithCorrectDetailsReturns200(String json) {
        Response loginResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier/login");
        loginResponse.then().statusCode(200)
                .and().assertThat().body("id", notNullValue());
    }

    @ParameterizedTest
    @MethodSource("incorrectCourierDetails")
    @DisplayName("Авторизация курьера в системе без логина или пароля")
    @Order(3)
    public void loginCourierWithoutLoginOrPasswordReturns400(String json) {
        Response loginResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier/login");
        loginResponse.then().statusCode(400)
                .and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация курьера в системе с несуществующим-логином-паролем")
    @Order(4)
    public void loginCourierWithIncorrectDetailsReturns404() {
        String json = "{\"login\": \"TestAutotest\", \"password\": \"111werty123\"}";
        Response loginResponse =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier/login");
        loginResponse.then().statusCode(404)
                .and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }


    @ParameterizedTest
    @MethodSource("correctCourierDetails")
    @DisplayName("Удаление курьеров после выполнения автотестов")
    @Order(5)
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

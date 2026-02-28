package couriers;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pojo.Courier;
import utils.BaseURL;
import utils.api.CourierApi;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest extends BaseURL {
    private CourierApi courierApi;
    private static List<Courier> testCreateCouriers = List.of(
            new Courier("courierAutotest1", "qwerty123", "DimaCourier"),
            new Courier("courierAutotest3", "qwerty456", "VasyaCourier"));
    private static List<Courier> testDeleteCouriers = List.of(
            new Courier("courierAutotest1", "qwerty123"),
            new Courier("courierAutotest3", "qwerty456"));

    public static List<Courier> courierDetailsForCreate() {
        return testCreateCouriers;
    }

    public static List<Courier> courierDetailsForDelete() {
        return testDeleteCouriers;
    }

    static Stream<Arguments> correctCourierDetails() {
        return testDeleteCouriers.stream().map(Arguments::of);
    }

    static Stream<Arguments> incorrectCourierDetails() {
        return Stream.of(
                Arguments.of(new Courier("courierAutotest1", "")),
                Arguments.of(new Courier("", "qwerty456"))
        );
    }

    @BeforeEach
    public void init() {
        courierApi = new CourierApi(requestSpec);
    }

    @BeforeAll
    @DisplayName("Создание курьера для проверки авторизации")
    public static void createCouriers() {
        CourierApi createCourierApi = new CourierApi(requestSpec);
        List<Courier> createCouriers = courierDetailsForCreate();

        for (Courier courier: createCouriers) {
            Response response = createCourierApi.createCourier(courier);

            response.then().assertThat().body("ok", equalTo(true))
                    .and()
                    .statusCode(201);
        }
    }

    @ParameterizedTest
    @MethodSource("correctCourierDetails")
    @DisplayName("Авторизация курьера в системе с валидным логином и паролем")
    public void loginCourierWithCorrectDetailsReturns200(Courier courier) {
        Response loginResponse = courierApi.loginCourier(courier);
        loginResponse.then().statusCode(200)
                .and().assertThat().body("id", notNullValue());
    }

    @ParameterizedTest
    @MethodSource("incorrectCourierDetails")
    @DisplayName("Авторизация курьера в системе без логина или пароля")
    public void loginCourierWithoutLoginOrPasswordReturns400(Courier courier) {
        Response loginResponse = courierApi.loginCourier(courier);
        loginResponse.then().statusCode(400)
                .and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация курьера в системе с несуществующим-логином-паролем")
    public void loginCourierWithIncorrectDetailsReturns404() {
        Courier courier = new Courier("TestAutotest", "111werty123");
        Response loginResponse = courierApi.loginCourier(courier);
        loginResponse.then().statusCode(404)
                .and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }


    @AfterAll
    @DisplayName("Удаление курьеров после выполнения автотестов")
    public static void deleteCourierDetailsAfterTest() {
        CourierApi cleanUpApi = new CourierApi(requestSpec);
        List<Courier> deleteCouriers = courierDetailsForDelete();

        for (Courier courier: deleteCouriers) {
            //Логин курьера, чтобы извлечь его id
            Response loginResponse = cleanUpApi.loginCourier(courier);
            loginResponse.then().statusCode(200);

            //Извлечение id курьера
            int courierId = loginResponse.jsonPath().getInt("id");

            //Удаление курьера, используя id
            Response deleteResponse = cleanUpApi.deleteCourier(courierId);
            deleteResponse.then().statusCode(200).body("ok", equalTo(true));
        }
    }
}

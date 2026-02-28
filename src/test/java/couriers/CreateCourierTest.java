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

public class CreateCourierTest extends BaseURL{
    private CourierApi courierApi;
    private static List<Courier> testCouriers;

    @BeforeEach
    public void init() {
        courierApi = new CourierApi(requestSpec);
    }

    static Stream<Arguments> correctCourierDetails() {
        return Stream.of(
                Arguments.of(new Courier("courierAutotest1", "qwerty123", "DimaCourier")),
                Arguments.of(new Courier("courierAutotest3", "qwerty456", "VasyaCourier"))
        );
    }

    static Stream<Arguments> incorrectCourierDetails() {
        return Stream.of(
                Arguments.of(new Courier("", "qwerty2323", "PetyaCourier")),
                Arguments.of(new Courier("courierAutotest4", "", "IgorCourier")),
                Arguments.of(new Courier("courierAutotest5", "qwerty555", ""))
        );
    }

    static List<Courier> courierDetailsForDeletion() {
        testCouriers = List.of(
                new Courier("courierAutotest1", "qwerty123"),
                new Courier("courierAutotest3", "qwerty456"),
                new Courier("courierAutotest5", "qwerty555"),
                new Courier("courierAutotest7", "jhgjhg")
        );

        return testCouriers;
    }

    @ParameterizedTest
    @MethodSource("correctCourierDetails")
    @DisplayName("Проверка создания курьера с валидными данными")
    public void createCourierWithValidDataReturnsOk(Courier courier) {
        Response response = courierApi.createCourier(courier);

        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    @DisplayName("Проверка создания двух одинаковых курьеров")
    public void createIdenticalCouriersReturns409() {
        Courier courier = new Courier("courierAutotest7", "jhgjhg", "MashaCourier");
        courierApi.createCourier(courier).then().statusCode(201);

        Response response = courierApi.createCourier(courier);
        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);
    }

    @ParameterizedTest
    @MethodSource("incorrectCourierDetails")
    @DisplayName("Создание курьера, если одного из обязательных полей нет ")
    public void createCourierWithoutARequiredFieldReturns400(Courier courier) {
        Response response = courierApi.createCourier(courier);

        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }

    @AfterAll
    @DisplayName("Удаление курьеров после выполнения автотестов")
    public static void deleteCourierDetailsAfterTest() {
        CourierApi cleanUpApi = new CourierApi(requestSpec);
        List<Courier> deleteCouriers = courierDetailsForDeletion();

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

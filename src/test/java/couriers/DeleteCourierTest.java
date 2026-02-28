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

public class DeleteCourierTest extends BaseURL {
    private CourierApi courierApi;
    private static List<Courier> testCreateCouriers = List.of(
            new Courier("courierAutotest1", "qwerty123", "DimaCourier"),
            new Courier("courierAutotest3", "qwerty456", "VasyaCourier"));

    public static List<Courier> courierDetailsForCreate() {
        return testCreateCouriers;
    }

    static Stream<Arguments> correctCourierDetails() {
        return java.util.stream.Stream.of(
                Arguments.of(new Courier("courierAutotest1", "qwerty123")),
                Arguments.of(new Courier("courierAutotest3", "qwerty456"))
        );
    }

    @BeforeEach
    public void init() {
        courierApi = new CourierApi(requestSpec);
    }

    @BeforeAll
    @DisplayName("Создание курьера для проверки последующего удаления курьера")
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

    @Test
    @DisplayName("Удаление курьера без корректного id")
    public void deleteCourierWithoutIdReturns400() {
        Response deleteResponse = courierApi.deleteCourier(-1);
        deleteResponse.then().statusCode(400).body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Удаление курьера с несуществующим id")
    public void deleteCourierWithNonExistentIdReturns404() {
        Response deleteResponse = courierApi.deleteCourier(00000);
        deleteResponse.then().statusCode(404).body("message", equalTo("Курьера с таким id нет."));
    }

    @ParameterizedTest
    @MethodSource("correctCourierDetails")
    @DisplayName("Успешное удаление курьеров")
    public void deleteCourierDetailsReturns200(Courier courier) {
        //Логин курьера, чтобы извлечь его id
        Response loginResponse = courierApi.loginCourier(courier);
        loginResponse.then().statusCode(200);

        //Извлечение id курьера
        int courierId = loginResponse.jsonPath().getInt("id");

        //Удаление курьера, используя id
        Response deleteResponse = courierApi.deleteCourier(courierId);
        deleteResponse.then().statusCode(200).body("ok", equalTo(true));
    }
}

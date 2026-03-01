package couriers;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pojo.Courier;
import utils.BaseURL;
import utils.api.CourierApi;
import static org.hamcrest.Matchers.equalTo;
import static utils.api.CourierApi.*;

public class CreateCourierTest extends BaseURL{
    private CourierApi courierApi;

    @BeforeEach
    public void init() {
        courierApi = new CourierApi(requestSpec);
    }

    @ParameterizedTest
    @MethodSource("testdata.CourierTestData#correctCourierDataForCreate")
    @DisplayName("Проверка создания курьера с валидными данными")
    public void createCourierWithValidDataReturnsOk(Courier courier) {
        createCourier(courierApi, courier);
        deleteCourierAfterTest(courierApi, courier);
    }

    @ParameterizedTest
    @MethodSource("testdata.CourierTestData#correctCourierDataForCreate")
    @DisplayName("Проверка создания двух одинаковых курьеров")
    public void createIdenticalCouriersReturns409(Courier courier) {
        createCourier(courierApi, courier);

        Response response = courierApi.createCourier(courier);
        response.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(409);

        deleteCourierAfterTest(courierApi, courier);
    }

    @ParameterizedTest
    @MethodSource("testdata.CourierTestData#incorrectCourierDataForCreate")
    @DisplayName("Проверка создания курьера, если одного из обязательных полей нет ")
    public void createCourierWithoutARequiredFieldReturns400(Courier courier) {
        Response response = courierApi.createCourier(courier);

        response.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(400);
    }
}

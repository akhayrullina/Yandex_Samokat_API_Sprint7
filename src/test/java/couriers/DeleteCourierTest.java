package couriers;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pojo.Courier;
import utils.BaseURL;
import utils.api.CourierApi;
import static org.hamcrest.Matchers.equalTo;
import static utils.api.CourierApi.createCourier;

public class DeleteCourierTest extends BaseURL {
    private CourierApi courierApi;

    @BeforeEach
    public void init() {
        courierApi = new CourierApi(requestSpec);
    }

    @Test
    @DisplayName("Проверка удаления курьера без корректного id")
    public void deleteCourierWithoutIdReturns400() {
        Response deleteResponse = courierApi.deleteCourier(-1);
        deleteResponse.then().statusCode(400).body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Проверка удаления курьера с несуществующим id")
    public void deleteCourierWithNonExistentIdReturns404() {
        Response deleteResponse = courierApi.deleteCourier(00000);
        deleteResponse.then().statusCode(404).body("message", equalTo("Курьера с таким id нет."));
    }

    @ParameterizedTest
    @MethodSource("testdata.CourierTestData#correctCourierDataForCreate")
    @DisplayName("Проверка успешного удаления созданных курьеров")
    public void deleteCourierDetailsReturns200(Courier courier) {
        createCourier(courierApi, courier);
        courierApi.deleteCourierByCredentials(courier).then().statusCode(200).body("ok", equalTo(true));
    }
}

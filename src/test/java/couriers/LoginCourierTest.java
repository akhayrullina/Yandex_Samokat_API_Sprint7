package couriers;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pojo.Courier;
import utils.BaseURL;
import utils.api.CourierApi;
import java.util.List;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static testdata.CourierTestData.*;
import static utils.api.CourierApi.*;

public class LoginCourierTest extends BaseURL {
    private CourierApi courierApi;

    @BeforeEach
    public void init() {
        courierApi = new CourierApi(requestSpec);
        createCouriers();
    }

    @Step("Создание курьеров для проверки авторизации")
    public static void createCouriers() {
        CourierApi createCourierApi = new CourierApi(requestSpec);
        List<Courier> createCouriers = List.of(COURIER_DIMA, COURIER_VASYA, COURIER_MASHA);
        for (Courier courier: createCouriers) {
            createCourier(createCourierApi, courier);
        }
    }

    @AfterEach
    @DisplayName("Удаление курьеров после выполнения автотеста")
    public void deleteCourier() {
        List<Courier> deleteCouriers = List.of(COURIER_DIMA_CREDENTIALS, COURIER_VASYA_CREDENTIALS, COURIER_MASHA_CREDENTIALS);
        for (Courier courier: deleteCouriers) {
            deleteCourierAfterTest(courierApi, courier);
        }
    }

    @ParameterizedTest
    @MethodSource("testdata.CourierTestData#correctCourierDataForLogin")
    @DisplayName("Проверка авторизации курьера в системе с валидным логином и паролем")
    public void loginCourierWithCorrectDataReturns200(Courier courier) {
        Response loginResponse = courierApi.loginCourier(courier);
        loginResponse.then().statusCode(200)
                .and().assertThat().body("id", notNullValue());
    }

    @ParameterizedTest
    @MethodSource("testdata.CourierTestData#incorrectCourierDataForLogin")
    @DisplayName("Проверка авторизации курьера в системе без логина или пароля")
    public void loginCourierWithoutLoginOrPasswordReturns400(Courier courier) {
        Response loginResponse = courierApi.loginCourier(courier);
        loginResponse.then().statusCode(400)
                .and().assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Проверка авторизации курьера в системе с несуществующим-логином-паролем")
    public void loginCourierWithIncorrectDetailsReturns404() {
        Response loginResponse = courierApi.loginCourier(COURIER_NOT_EXIST);
        loginResponse.then().statusCode(404)
                .and().assertThat().body("message", equalTo("Учетная запись не найдена"));
    }
}

package testdata;

import org.junit.jupiter.params.provider.Arguments;
import pojo.Courier;
import java.util.stream.Stream;

public class CourierTestData {

    // Курьеры для создания (с firstName)
    public static final Courier COURIER_DIMA = new Courier("courierAutotest1", "qwerty123", "DimaCourier");
    public static final Courier COURIER_VASYA = new Courier("courierAutotest3", "qwerty456", "VasyaCourier");
    public static final Courier COURIER_MASHA = new Courier("courierAutotest7", "jhgjhg", "MashaCourier");

    // Курьеры для логина (только login и password)
    public static final Courier COURIER_DIMA_CREDENTIALS = new Courier("courierAutotest1", "qwerty123");
    public static final Courier COURIER_VASYA_CREDENTIALS = new Courier("courierAutotest3", "qwerty456");
    public static final Courier COURIER_MASHA_CREDENTIALS = new Courier("courierAutotest7", "jhgjhg");

    //Курьеры с некорректными данными для создания
    public static final Courier COURIER_EMPTY_LOGIN = new Courier("", "qwerty2323", "PetyaCourier");
    public static final Courier COURIER_EMPTY_PASSWORD = new Courier("courierAutotest4", "", "IgorCourier");
    public static final Courier COURIER_EMPTY_FIRSTNAME = new Courier("courierAutotest5", "qwerty555", "");

    // Курьеры с некорректными данными для логина
    public static final Courier COURIER_EMPTY_PASSWORD_FOR_LOGIN = new Courier("courierAutotest1", "");
    public static final Courier COURIER_EMPTY_LOGIN_FOR_LOGIN = new Courier("", "qwerty456");
    public static final Courier COURIER_NOT_EXIST = new Courier("TestAutotest", "111werty123");


    public static Stream<Arguments> correctCourierDataForCreate() {
        return Stream.of(
                Arguments.of(COURIER_DIMA),
                Arguments.of(COURIER_VASYA),
                Arguments.of(COURIER_MASHA)
        );
    }

    public static Stream<Arguments> incorrectCourierDataForCreate() {
        return Stream.of(
                Arguments.of(COURIER_EMPTY_LOGIN),
                Arguments.of(COURIER_EMPTY_PASSWORD),
                Arguments.of(COURIER_EMPTY_FIRSTNAME)
        );
    }

    public static Stream<Arguments> correctCourierDataForLogin() {
        return Stream.of(
                Arguments.of(COURIER_DIMA_CREDENTIALS),
                Arguments.of(COURIER_VASYA_CREDENTIALS),
                Arguments.of(COURIER_MASHA_CREDENTIALS)
        );
    }

    public static Stream<Arguments> incorrectCourierDataForLogin() {
        return Stream.of(
                Arguments.of(COURIER_EMPTY_LOGIN_FOR_LOGIN),
                Arguments.of(COURIER_EMPTY_PASSWORD_FOR_LOGIN)
        );
    }
}

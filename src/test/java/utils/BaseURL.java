package utils;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.SSLConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;

public class BaseURL {
    public static RequestSpecification requestSpec;

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";

        // Настраиваем RestAssured на игнорирование SSL ошибок
        RestAssured.config = RestAssured.config()
                .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation());

        //Настройка спецификации
        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(RestAssured.baseURI)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
    }
}

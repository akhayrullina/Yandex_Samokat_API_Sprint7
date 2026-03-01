package testdata;

import org.junit.jupiter.params.provider.Arguments;
import pojo.Order;
import java.util.stream.Stream;

public class OrderTestData {
    public static final Order ORDER_1_COLOR = new Order("Вася", "Иванов",
            "г. Москва, ул. Нестерова, д. 142, кв. 7",
            "4", "+7 917 355 35 35",
            "5", "2026-06-06",
            "Привезите рабочий самокат", new String[]{"BLACK"});
    public static final Order ORDER_2_COLOR = new Order("Мария", "Митрошина",
            "г. Москва, ул. Садовая, д. 19, кв. 56",
            "6", "+7 917 555 35 35",
            "5", "2026-07-06",
            "Самокат любого цвета", new String[]{"BLACK", "GREY"});
    public static final Order ORDER_0_COLOR = new Order("Анастасия", "Горшкова",
            "г. Москва, ул. Номостовая, д. 87, кв. 98",
            "8", "+7 917 888 35 35",
            "5", "2026-08-06",
            "Самокат любого цвета", new String[]{""});

    public static Stream<Arguments> orderDetailsForCreate() {
        return Stream.of(
                Arguments.of(ORDER_1_COLOR),
                Arguments.of(ORDER_2_COLOR),
                Arguments.of(ORDER_0_COLOR)
        );
    }
}

package orders;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.BaseURL;
import utils.api.OrderApi;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import pojo.Order;

public class CreateOrderTest extends BaseURL {
    private OrderApi orderApi;
    private static ArrayList<Integer> ordersTrack = new ArrayList<>();
    private static List<Order> testCreateOrders = List.of(
            new Order("Вася", "Иванов",
                    "г. Москва, ул. Нестерова, д. 142, кв. 7",
                    "4", "+7 917 355 35 35",
                    "5", "2026-06-06",
                    "Привезите рабочий самокат", new String[]{"BLACK"}),
            new Order("Мария", "Митрошина",
                    "г. Москва, ул. Садовая, д. 19, кв. 56",
                    "6", "+7 917 555 35 35",
                    "5", "2026-07-06",
                    "Самокат любого цвета",
                    new String[]{"BLACK", "GREY"} ),
            new Order("Анастасия", "Горшкова",
                    "г. Москва, ул. Номостовая, д. 87, кв. 98" ,
                    "8", "+7 917 888 35 35",
                    "5", "2026-08-06",
                    "Самокат любого цвета", new String[]{""})
    );

    public static List<Order> orderDetailsForCreate() {
        return testCreateOrders;
    }

    @BeforeEach
    public void init() {
        orderApi = new OrderApi(requestSpec);
    }

    @Test
    @DisplayName("Проверка создания заказа с валидными данными")
    public void createOrderWithValidDataReturnsOk() {
        List<Order> createOrders = orderDetailsForCreate();

        for (Order order: createOrders) {
            Response orderResponse = orderApi.createOrder(order);

            orderResponse.then().assertThat().body("track", notNullValue())
                    .and()
                    .statusCode(201);

            int track = orderResponse.jsonPath().getInt("track");
            ordersTrack.add(track);
            System.out.println(ordersTrack);
        }
    }



    @AfterAll
    @DisplayName("Удаление заказов после выполнения автотестов")
    public static void deleteOrderAfterTest() {
        OrderApi deleteOrderApi = new OrderApi(requestSpec);

        for (int i = 0; i < ordersTrack.size(); i++) {
            String json = "{\"track\": " + ordersTrack.get(i) + "}";
            Response deleteResponse = deleteOrderApi.cancelOrder(json);
            deleteResponse.then().statusCode(200).body("ok", equalTo(true));
        }
    }
}

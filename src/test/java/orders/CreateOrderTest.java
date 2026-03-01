package orders;

import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import utils.BaseURL;
import utils.api.OrderApi;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static testdata.OrderTestData.*;
import pojo.Order;

public class CreateOrderTest extends BaseURL {
    private OrderApi orderApi;
    private static ArrayList<Integer> ordersTrack = new ArrayList<>();

    @BeforeEach
    public void init() {
        orderApi = new OrderApi(requestSpec);
    }

    @Test
    @DisplayName("Проверка создания заказа с валидными данными")
    public void createOrderWithValidDataReturnsOk() {
        List<Order> createOrders = List.of(ORDER_0_COLOR, ORDER_1_COLOR, ORDER_2_COLOR);

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

package orders;

import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.BaseURL;
import utils.api.OrderApi;


public class ListOfOrders extends BaseURL {
    private OrderApi orderApi;

    @BeforeEach
    public void init() {
        orderApi = new OrderApi(requestSpec);
    }

    @Test
    @DisplayName("Проверка получения списка заказов")
    public void getListOfOrders() {
        Response response = orderApi.getOrders();
        response.then().statusCode(200);
    }
}

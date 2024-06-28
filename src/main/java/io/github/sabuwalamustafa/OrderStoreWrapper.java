package io.github.sabuwalamustafa;

import io.github.sabuwalamustafa.models.DatabaseConfig;
import io.github.sabuwalamustafa.models.OrderInternal;

import java.util.List;

public class OrderStoreWrapper {
    private OrderStore orderStore;

    public OrderStoreWrapper(DatabaseConfig databaseConfig) {
        this.orderStore = new OrderStore(databaseConfig.getJdbcUrl(),
                                         databaseConfig.getUsername(),
                                         databaseConfig.getPassword());
    }

    public void onSuccessfulOrderPlacement(OrderInternal orderInternal) {
        // todo: failure handling?
        orderStore.createOrderEntry(orderInternal);
    }

    public OrderInternal getOrder(String orderId) {
        return orderStore.readOrderEntry(orderId);
    }

    public List<OrderInternal> getAllOrders() throws Exception {
        return orderStore.listAllOrderEntries();
    }
}

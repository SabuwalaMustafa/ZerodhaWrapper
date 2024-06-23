package io.github.sabuwalamustafa;

import io.github.sabuwalamustafa.models.OrderInternal;

import java.util.List;

public class OrderStoreWrapper {
    private OrderStore orderStore;

    public OrderStoreWrapper() {
        this.orderStore = new OrderStore();
    }

    public void onSuccessfulOrderPlacement(OrderInternal orderInternal) {
        orderStore.createOrderEntry(orderInternal);
    }

    public OrderInternal getOrder(String orderId) {
        return orderStore.readOrderEntry(orderId);
    }

    public List<OrderInternal> getAllOrders() {
        return orderStore.listAllOrderEntries();
    }
}

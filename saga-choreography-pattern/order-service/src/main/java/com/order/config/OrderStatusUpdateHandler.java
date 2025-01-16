package com.order.config;

import com.commons.dto.OrderRequestDto;
import com.commons.event.OrderStatus;
import com.commons.event.PaymentStatus;
import com.order.entity.PurchaseOrder;
import com.order.repository.OrderRepository;
import com.order.service.OrderStatusPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class OrderStatusUpdateHandler {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusPublisher orderStatusPublisher;

    @Transactional
    public void updateOrder(String orderId, Consumer<PurchaseOrder> consumer) {
        orderRepository.findById(orderId).ifPresent(consumer.andThen(this::updateOrder));
    }

    private void updateOrder(PurchaseOrder purchaseOrder) {
        boolean isPaymentCompleted = PaymentStatus.PAYMENT_COMPLETED
                .equals(purchaseOrder.getPaymentStatus());
        OrderStatus orderStatus = isPaymentCompleted ? OrderStatus.ORDER_COMPLETED :
                OrderStatus.ORDER_CANCELLED;
        log.info("Order status updated to {}", orderStatus);
        purchaseOrder.setOrderStatus(orderStatus);

        if(!isPaymentCompleted) {
            orderStatusPublisher.produceKafkaEvent(convertToOrderRequestDto(purchaseOrder), orderStatus);
        }
    }

    private OrderRequestDto convertToOrderRequestDto(PurchaseOrder purchaseOrder) {
        return OrderRequestDto.builder().orderId(purchaseOrder.getId()).userId(purchaseOrder.getUserId())
                .amount(purchaseOrder.getPrice())
                .productId(purchaseOrder.getProductId()).build();
    }

}

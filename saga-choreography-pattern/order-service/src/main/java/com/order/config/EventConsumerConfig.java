package com.order.config;

import com.commons.event.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class EventConsumerConfig {

    @Autowired
    private OrderStatusUpdateHandler handler;

    public Consumer<PaymentEvent> paymentConsumer() {
        return paymentEvent -> handler.updateOrder(
                paymentEvent.getPaymentRequestDto().getOrderId(),
                order -> order.setPaymentStatus(paymentEvent.getPaymentStatus()));
    }
}

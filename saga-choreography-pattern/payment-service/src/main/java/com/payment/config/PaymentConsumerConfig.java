package com.payment.config;

import com.commons.event.OrderEvent;
import com.commons.event.OrderStatus;
import com.commons.event.PaymentEvent;
import com.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Configuration
public class PaymentConsumerConfig {

    @Autowired
    private PaymentService paymentService;

    @Bean
    public Function<Flux<OrderEvent>, Flux<PaymentEvent>> paymentConsumer() {
        return orderEventFlux -> orderEventFlux.flatMap(this::processEvent);
    }

    private Mono<PaymentEvent> processEvent(OrderEvent orderEvent) {
        if(OrderStatus.ORDER_CREATED.equals(orderEvent.getOrderStatus())) {
            return Mono.fromSupplier(() -> paymentService.newOrderEvent(orderEvent));
        }else {
            return Mono.fromRunnable(() -> paymentService.cancelOrderEvent(orderEvent));
        }
    }
}

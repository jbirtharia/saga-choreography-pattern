package com.order.service;

import com.commons.dto.OrderRequestDto;
import com.commons.event.OrderEvent;
import com.commons.event.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

@Service
public class OrderStatusPublisher {

    @Autowired
    private Sinks.Many<OrderEvent> orderEventSink;

    public void produceKafkaEvent(OrderRequestDto orderRequestDto, OrderStatus orderStatus) {
        OrderEvent orderEvent = new OrderEvent(orderRequestDto, orderStatus);
        orderEventSink.tryEmitNext(orderEvent);
    }

}
package com.order.service;

import com.commons.dto.OrderRequestDto;
import com.commons.event.OrderStatus;
import com.order.entity.PurchaseOrder;
import com.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusPublisher orderStatusPublisher;

    @Transactional
    public PurchaseOrder createOrder(OrderRequestDto orderRequestDto) {
        PurchaseOrder purchaseOrder = orderRepository
                .save(covertOrderDtoToOrderEntity(orderRequestDto));
        orderRequestDto.setOrderId(purchaseOrder.getId());

        /// Produce kafka event with status ORDER_CREATED
        orderStatusPublisher.produceKafkaEvent(orderRequestDto, OrderStatus.ORDER_CREATED);
        return purchaseOrder;
    }

    public List<PurchaseOrder> getOrders() {
        return orderRepository.findAll();
    }

    private PurchaseOrder covertOrderDtoToOrderEntity(OrderRequestDto orderRequestDto) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setUserId(orderRequestDto.getUserId());
        purchaseOrder.setProductId(orderRequestDto.getProductId());
        purchaseOrder.setPrice(orderRequestDto.getAmount());
        purchaseOrder.setOrderStatus(OrderStatus.ORDER_CREATED);
        return purchaseOrder;
    }
}

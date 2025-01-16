package com.order.controller;

import com.commons.dto.OrderRequestDto;
import com.order.entity.PurchaseOrder;
import com.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping
    public ResponseEntity<PurchaseOrder> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.createOrder(orderRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getOrders() {
        return ResponseEntity.ok(orderService.getOrders());
    }
}

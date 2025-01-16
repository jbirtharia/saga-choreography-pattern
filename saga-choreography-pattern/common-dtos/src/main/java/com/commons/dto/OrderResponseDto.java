package com.commons.dto;

import com.commons.event.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private Integer productId;
    private String userId;
    private Integer amount;
    private String orderId;
    private OrderStatus orderStatus;
}

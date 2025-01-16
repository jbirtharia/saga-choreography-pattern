package com.payment.service;

import com.commons.dto.OrderRequestDto;
import com.commons.dto.PaymentRequestDto;
import com.commons.event.OrderEvent;
import com.commons.event.PaymentEvent;
import com.commons.event.PaymentStatus;
import com.payment.entity.UserBalance;
import com.payment.entity.UserTransaction;
import com.payment.exception.OrderNotFoundException;
import com.payment.exception.UserNotFoundException;
import com.payment.repository.UserBalanceRepository;
import com.payment.repository.UserTransactionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
public class PaymentService {

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserTransactionRepository userTransactionRepository;

    @Transactional
    public PaymentEvent newOrderEvent(OrderEvent orderEvent) {
        OrderRequestDto orderRequestDto = orderEvent.getOrderRequestDto();
        PaymentRequestDto paymentRequestDto = createPaymentRequestDto(orderRequestDto);
        if(userBalanceRepository.findById(orderRequestDto.getUserId()).isPresent()) {
               return userBalanceRepository.findById(orderRequestDto.getUserId())
                    .filter(u -> u.getPrice() >= orderRequestDto.getAmount())
                    .map(u -> {
                        u.setPrice(u.getPrice() - orderRequestDto.getAmount());
                        saveUserTransaction(orderRequestDto);
                        userBalanceRepository.save(u);
                        return PaymentEvent.builder().paymentRequestDto(paymentRequestDto)
                                .paymentStatus(PaymentStatus.PAYMENT_COMPLETED).build();
               }).orElse(PaymentEvent.builder().paymentRequestDto(paymentRequestDto)
                               .paymentStatus(PaymentStatus.PAYMENT_FAILED).build());
        }else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Transactional
    public void cancelOrderEvent(OrderEvent orderEvent) {
        if(userBalanceRepository.findById(orderEvent.getOrderRequestDto().getOrderId()).isPresent()) {
            userBalanceRepository.findById(orderEvent.getOrderRequestDto().getOrderId()).ifPresent(u -> {
                userBalanceRepository.delete(u);
                userTransactionRepository.findById(u.getUserId())
                        .ifPresent(ub ->
                                ub.setAmount(ub.getAmount() + u.getPrice()));
            });
        }else {
            throw new OrderNotFoundException("Order not found");
        }
    }

    private void saveUserTransaction(OrderRequestDto orderRequestDto) {
        userTransactionRepository.save(
                UserTransaction.builder()
                        .userId(orderRequestDto.getUserId())
                        .amount(orderRequestDto.getAmount())
                        .orderId(orderRequestDto.getOrderId()).build());
    }

    private static PaymentRequestDto createPaymentRequestDto(OrderRequestDto orderRequestDto) {
        return PaymentRequestDto.builder().userId(orderRequestDto.getUserId())
                        .orderId(orderRequestDto.getOrderId())
                        .amount(orderRequestDto.getAmount()).build();
    }

    @PostConstruct
    public void initUserBalanceInDB() {
        userBalanceRepository.saveAll(Stream.of(
                UserBalance.builder().price(3000).build(),
                UserBalance.builder().price(4200).build(),
                UserBalance.builder().price(20000).build(),
                UserBalance.builder().price(999).build()).toList());
    }

}

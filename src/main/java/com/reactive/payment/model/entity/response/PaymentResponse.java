package com.reactive.payment.model.entity.response;

import com.reactive.payment.model.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String id;

    private String orderId;

    private String customerName;

    private BigDecimal amount;

    private PaymentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
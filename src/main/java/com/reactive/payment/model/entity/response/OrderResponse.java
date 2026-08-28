package com.reactive.payment.model.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String id;

    private String customerName;

    private Long productId;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private String status;

    private String paymentId;
}
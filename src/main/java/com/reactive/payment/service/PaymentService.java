package com.reactive.payment.service;

import com.reactive.payment.model.entity.request.PaymentRequest;
import com.reactive.payment.model.entity.response.PaymentResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentService {

    Mono<PaymentResponse> processPayment(PaymentRequest request);

    Flux<PaymentResponse> getAllPayments();

    Mono<PaymentResponse> getPaymentById(String id);
}
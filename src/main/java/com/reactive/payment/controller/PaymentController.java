package com.reactive.payment.controller;

import com.reactive.payment.model.entity.request.PaymentRequest;
import com.reactive.payment.model.entity.response.ApiResponse;
import com.reactive.payment.model.entity.response.PaymentResponse;
import com.reactive.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<PaymentResponse>>> processPayment(
            @Valid @RequestBody PaymentRequest request) {

        return paymentService.processPayment(request)
                .map(payment ->
                        ResponseEntity.status(HttpStatus.CREATED)
                                .body(
                                        new ApiResponse<>(
                                                "Payment processed successfully",
                                                payment
                                        )
                                )
                );
    }

    @GetMapping
    public Mono<ResponseEntity<ApiResponse<Flux<PaymentResponse>>>> getAllPayments() {

        return Mono.just(
                ResponseEntity.ok(
                        new ApiResponse<>(
                                "Payments fetched successfully",
                                paymentService.getAllPayments()
                        )
                )
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ApiResponse<PaymentResponse>>> getPaymentById(
            @PathVariable String id) {

        return paymentService.getPaymentById(id)
                .map(payment ->
                        ResponseEntity.ok(
                                new ApiResponse<>(
                                        "Payment fetched successfully",
                                        payment
                                )
                        )
                );
    }
}
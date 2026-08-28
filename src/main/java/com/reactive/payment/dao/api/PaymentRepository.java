package com.reactive.payment.dao.api;

import com.reactive.payment.model.entity.Payment;
import com.reactive.payment.model.entity.PaymentStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentRepository extends ReactiveMongoRepository<Payment, String> {

    Mono<Payment> findByOrderId(String orderId);

    Flux<Payment> findByStatus(PaymentStatus status);
}
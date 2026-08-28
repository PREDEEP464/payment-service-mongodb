package com.reactive.payment.serviceImpl;

import com.reactive.payment.client.order.OrderServiceClient;
import com.reactive.payment.dao.api.PaymentRepository;
import com.reactive.payment.exception.InvalidPaymentStateException;
import com.reactive.payment.exception.PaymentNotFoundException;
import com.reactive.payment.model.entity.Payment;
import com.reactive.payment.model.entity.PaymentStatus;
import com.reactive.payment.model.entity.request.PaymentRequest;
import com.reactive.payment.model.entity.response.OrderResponse;
import com.reactive.payment.model.entity.response.PaymentResponse;
import com.reactive.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;

    @Override
    public Mono<PaymentResponse> processPayment(PaymentRequest request) {

        return paymentRepository.findByOrderId(request.getOrderId())
                .flatMap(existingPayment ->
                        Mono.<Payment>error(
                                new InvalidPaymentStateException(
                                        "Payment already exists for order: "
                                                + request.getOrderId()
                                )
                        )
                )
                .switchIfEmpty(
                        Mono.defer(() ->
                                orderServiceClient.getOrderById(
                                                request.getOrderId()
                                        )
                                        .switchIfEmpty(
                                                Mono.error(
                                                        new PaymentNotFoundException(
                                                                "Order not found with id: "
                                                                        + request.getOrderId()
                                                        )
                                                )
                                        )
                                        .filter(order ->
                                                "PAYMENT_PENDING".equals(
                                                        order.getStatus()
                                                )
                                        )
                                        .switchIfEmpty(
                                                Mono.error(
                                                        new InvalidPaymentStateException(
                                                                "Order is not in PAYMENT_PENDING state"
                                                        )
                                                )
                                        )
                                        .flatMap(order ->
                                                createPayment(
                                                        request,
                                                        order
                                                )
                                        )
                        )
                )
                .map(this::convertToResponse)
                .doOnNext(payment ->
                        System.out.println(
                                "Payment processed: " + payment.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while processing payment: "
                                        + error.getMessage()
                        )
                );
    }

    private Mono<Payment> createPayment(
            PaymentRequest request,
            OrderResponse order) {

        return Mono.defer(() -> {

            Payment payment = new Payment();

            payment.setOrderId(order.getId());
            payment.setCustomerName(order.getCustomerName());
            payment.setAmount(request.getAmount());
            payment.setCreatedAt(LocalDateTime.now());
            payment.setUpdatedAt(LocalDateTime.now());

            // Compare payment amount with actual order total
            if (request.getAmount().compareTo(order.getTotalAmount()) != 0) {

                payment.setStatus(PaymentStatus.FAILED);

                return paymentRepository.save(payment)
                        .flatMap(savedPayment ->
                                orderServiceClient.paymentFailed(
                                                savedPayment.getOrderId()
                                        )
                                        .thenReturn(savedPayment)
                        );
            }

            payment.setStatus(PaymentStatus.SUCCESS);

            return paymentRepository.save(payment)
                    .flatMap(savedPayment ->
                            orderServiceClient.confirmOrder(
                                            savedPayment.getOrderId()
                                    )
                                    .thenReturn(savedPayment)
                    );
        });
    }

    @Override
    public Flux<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .map(this::convertToResponse)
                .doOnNext(payment ->
                        System.out.println(
                                "Payment fetched: " + payment.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while fetching payments: "
                                        + error.getMessage()
                        )
                );
    }

    @Override
    public Mono<PaymentResponse> getPaymentById(String id) {

        return paymentRepository.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new PaymentNotFoundException(
                                        "Payment not found with id: " + id
                                )
                        )
                )
                .map(this::convertToResponse)
                .doOnNext(payment ->
                        System.out.println(
                                "Payment found: " + payment.getId()
                        )
                )
                .doOnError(error ->
                        System.err.println(
                                "Error while fetching payment: "
                                        + error.getMessage()
                        )
                );
    }

    private PaymentResponse convertToResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerName(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}
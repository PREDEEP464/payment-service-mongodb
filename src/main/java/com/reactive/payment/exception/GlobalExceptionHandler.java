package com.reactive.payment.exception;

import com.reactive.payment.model.entity.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handlePaymentNotFound(
            PaymentNotFoundException ex) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                new ApiResponse<>(
                                        ex.getMessage(),
                                        null
                                )
                        )
        );
    }

    @ExceptionHandler(InvalidPaymentStateException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleInvalidPaymentState(
            InvalidPaymentStateException ex) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new ApiResponse<>(
                                        ex.getMessage(),
                                        null
                                )
                        )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return Mono.just(
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(
                                new ApiResponse<>(
                                        ex.getMessage(),
                                        null
                                )
                        )
        );
    }
}
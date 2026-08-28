package com.reactive.payment.client.order;

import com.reactive.payment.model.entity.response.ApiResponse;
import com.reactive.payment.model.entity.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {

    private final WebClient webClient;

    @Value("${order.service.url}")
    private String orderServiceUrl;

    public Mono<OrderResponse> getOrderById(String orderId) {

        return webClient.get()
                .uri(orderServiceUrl + "/" + orderId)
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<ApiResponse<OrderResponse>>() {
                        }
                )
                .map(ApiResponse::getData);
    }

    public Mono<OrderResponse> confirmOrder(String orderId) {

        return webClient.patch()
                .uri(orderServiceUrl + "/" + orderId + "/confirm")
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<ApiResponse<OrderResponse>>() {
                        }
                )
                .map(ApiResponse::getData);
    }

    public Mono<OrderResponse> paymentFailed(String orderId) {

        return webClient.patch()
                .uri(orderServiceUrl + "/" + orderId + "/payment-failed")
                .retrieve()
                .bodyToMono(
                        new ParameterizedTypeReference<ApiResponse<OrderResponse>>() {
                        }
                )
                .map(ApiResponse::getData);
    }
}
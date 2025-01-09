package com.example.app.api;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import com.example.app.exceptions.APIException;

public class APIService<T> {

    private final String baseUrl;
    private final WebClient webClient;

    // Constructor
    public APIService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.webClient = WebClient.builder()
                                  .baseUrl(baseUrl)
                                  .build();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Mono<T> callApi(String endpoint, Class<T> responseType) throws APIException {
        return webClient.get()
                        .uri(baseUrl + endpoint)
                        .retrieve()
                        .bodyToMono(responseType)
                        .onErrorResume(WebClientResponseException.class, ex -> {
                            // Handle 404 errors gracefully
                            if (ex.getStatusCode().value() == 404) {
                                return Mono.empty();
                            }
                            // Wrap other exceptions into a custom APIException
                            return Mono.error(new APIException("Error calling API: " + ex.getMessage(), ex));
                        });
    }
}
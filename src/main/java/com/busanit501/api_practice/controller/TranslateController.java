package com.busanit501.api_practice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TranslateController {

    @Value("${google.api.key}")
    private String googleApiKey;

    private final WebClient webClient;

    public TranslateController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @PostMapping("/translate")
    public Mono<String> translate(@RequestBody Map<String, String> request) {
        System.out.println("request = " + request);
        String text = request.get("text");
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("translation.googleapis.com")
                        .path("/language/translate/v2")
                        .queryParam("key", googleApiKey)
                        .queryParam("q", text)
                        .queryParam("source", "ko")
                        .queryParam("target", "en")
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}
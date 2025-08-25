package com.busanit501.api_practice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class TranslateController {

    @Value("${google.api.key}")
    private String googleApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public TranslateController(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @PostMapping("/translate")
    public Mono<TranslateResponse> translate(@RequestBody TranslateRequest request) {
        System.out.println("request = " + request);
        String text = request.getText();
        String sourceLanguage = request.getSourceLanguage();
        String targetLanguage = request.getTargetLanguage();

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("translation.googleapis.com")
                        .path("/language/translate/v2")
                        .queryParam("key", googleApiKey)
                        .queryParam("q", text)
                        .queryParam("source", sourceLanguage)
                        .queryParam("target", targetLanguage)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(jsonString -> {
                    try {
                        JsonNode rootNode = objectMapper.readTree(jsonString);
                        if (rootNode.has("error")) {
                            String errorMessage = rootNode.path("error").path("message").asText();
                            return Mono.error(new RuntimeException("Google API Error: " + errorMessage));
                        }
                        String translatedText = rootNode.path("data").path("translations").get(0).path("translatedText").asText();
                        return Mono.just(new TranslateResponse(translatedText));
                    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                        // Consider more robust error handling, e.g., throwing a custom exception
                        return Mono.error(new RuntimeException("Failed to parse translation response: " + e.getMessage(), e));
                    }
                });
    }
}
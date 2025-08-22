package com.busanit501.api_practice.controller;

import lombok.Data;

@Data
public class TranslateRequest {
    private String text;
    private String sourceLanguage;
    private String targetLanguage;
}

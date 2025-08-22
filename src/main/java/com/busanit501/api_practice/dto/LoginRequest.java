package com.busanit501.api_practice.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

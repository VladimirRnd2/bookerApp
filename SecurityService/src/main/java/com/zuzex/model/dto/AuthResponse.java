package com.zuzex.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class AuthResponse {

    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;
}

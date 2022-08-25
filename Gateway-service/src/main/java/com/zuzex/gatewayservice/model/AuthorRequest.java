package com.zuzex.gatewayservice.model;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthorRequest {

    @NotBlank
    private String authorName;
}

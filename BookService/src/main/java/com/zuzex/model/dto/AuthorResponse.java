package com.zuzex.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AuthorResponse {

    @NotBlank
    @Size(max = 128)
    private String name;

    public AuthorResponse(String name) {
        this.name = name;
    }

    public AuthorResponse() {
    }
}

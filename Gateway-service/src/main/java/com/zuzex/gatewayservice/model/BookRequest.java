package com.zuzex.gatewayservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BookRequest {

    @JsonProperty("title")
    @NotBlank
    private String title;

    public BookRequest(String title) {
        this.title = title;
    }

    public BookRequest() {
    }
}
package com.zuzex.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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

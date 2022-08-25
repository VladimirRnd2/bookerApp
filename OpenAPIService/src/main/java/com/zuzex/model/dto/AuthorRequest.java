package com.zuzex.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AuthorRequest {

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @NotBlank
    String authorName;
}

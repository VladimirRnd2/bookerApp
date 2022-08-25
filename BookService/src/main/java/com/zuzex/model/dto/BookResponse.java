package com.zuzex.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookResponse {

    private String title;
    private String date;
    private List<AuthorResponse> authors;

    public List<AuthorResponse> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorResponse> authors) {
        this.authors = authors;
    }
}

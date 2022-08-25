package com.zuzex.gatewayservice.model;

import lombok.Data;

import java.util.List;

@Data
public class UserRs {
    private Long id;
    private String login;
    private List<BookRs> books;

    public UserRs(String login) {
        this.login = login;
    }

    public UserRs() {
    }
}

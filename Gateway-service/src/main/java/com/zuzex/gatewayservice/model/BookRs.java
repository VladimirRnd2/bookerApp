package com.zuzex.gatewayservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class BookRs {

    private Long id;
    private String title;
    private String date;
    private Boolean isRead;
    private List<AuthorRs> authors;
    @JsonIgnore
    private List<UserRs> users;
}

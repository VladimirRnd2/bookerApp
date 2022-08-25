package com.zuzex.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseBook {
    private Long id;
    private String title;
    private String date;
    private Boolean isRead;
    private List<ResponseAuthor> authors;
    private List<ResponseUser> users;
}

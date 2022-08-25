package com.zuzex.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zuzex.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    private Long id;
    private String title;
    private String date;
    private Boolean isRead;
    private List<Author> authors;
    @JsonIgnore
    private List<User> users;
}

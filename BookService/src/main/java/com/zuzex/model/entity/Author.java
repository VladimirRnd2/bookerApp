package com.zuzex.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table(value = "authors")
@Data
@ToString
public class Author extends BaseEntity {

    private String name;

    @JsonIgnore
    private List<Book> books;
}

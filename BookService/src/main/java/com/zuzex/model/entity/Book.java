package com.zuzex.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table(value = "books")
@Data
@ToString
public class Book extends BaseEntity {

    private String title;
    private String date;
    private Boolean isRead;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(name = "books_authors",
//            joinColumns = {@JoinColumn(name = "book_id", referencedColumnName = "id")},
//            inverseJoinColumns = {@JoinColumn(name = "author_id", referencedColumnName = "id")})
    private List<Author> authors;

    @JsonIgnore
//    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
    private List<User> users;
}

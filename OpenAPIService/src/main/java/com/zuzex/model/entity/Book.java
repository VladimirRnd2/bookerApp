package com.zuzex.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@Table(name = "books")
@Data
@ToString
public class Book extends BaseEntity{

    @Column(name = "title")
    private String title;
    @Column(name = "date")
    private String date;
    @Column(name = "is_read")
    private Boolean isRead;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "books_authors",
            joinColumns = {@JoinColumn(name = "book_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id", referencedColumnName = "id")})
    private List<Author> authors;


    @JsonIgnore

    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
    private List<User> users;
}

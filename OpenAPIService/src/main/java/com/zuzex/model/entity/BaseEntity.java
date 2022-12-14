package com.zuzex.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created")
    @JsonIgnore
    private Date created;

    @LastModifiedDate
    @Column(name = "updated")
    @JsonIgnore
    private Date updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @JsonIgnore
    private Status status;
}

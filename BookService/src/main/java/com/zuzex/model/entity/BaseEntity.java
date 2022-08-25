package com.zuzex.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import java.util.Date;

@Data
public class BaseEntity {

    @Id
    private Long id;

    @CreatedDate
    @JsonIgnore
    private Date created;

    @LastModifiedDate
    @JsonIgnore
    private Date updated;

    @JsonIgnore
    private Status status;
}

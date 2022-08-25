package com.zuzex.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogDto {
    private String entityType;
    private String name;
    private Date currentDate;
    private String method;
    private String service;
}

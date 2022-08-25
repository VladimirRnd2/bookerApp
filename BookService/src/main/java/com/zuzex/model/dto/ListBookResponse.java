package com.zuzex.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ListBookResponse {

    List<BookResponse> bookResponseList;
}

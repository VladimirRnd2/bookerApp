package com.zuzex.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegistrationRequest {

    @NotBlank
    @Size(min = 2, max = 128)
    private String login;

    @NotBlank
    @Size(min= 8 , max = 128)
    private String password;

    @NotBlank
    @Size(min = 2, max = 128)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 128)
    private String lastName;
}

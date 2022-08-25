package com.zuzex.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuzex.model.dto.RegistrationRequest;
import com.zuzex.model.entity.User;
import com.zuzex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public User registerUser(@RequestBody RegistrationRequest request) {
        return userService.registerNewUser(request);
    }
}

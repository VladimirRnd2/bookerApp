package com.zuzex.service;

import com.zuzex.model.dto.RegistrationRequest;
import com.zuzex.model.entity.User;

public interface UserService {

    User registerNewUser(RegistrationRequest request);
}

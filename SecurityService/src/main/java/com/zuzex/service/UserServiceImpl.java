package com.zuzex.service;

import com.zuzex.model.dto.RegistrationRequest;
import com.zuzex.model.entity.Role;
import com.zuzex.model.entity.Status;
import com.zuzex.model.entity.User;
import com.zuzex.repository.RoleRepository;
import com.zuzex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public User registerNewUser(RegistrationRequest request) {
        User user = new User();
        user.setStatus(Status.ACTIVE);
        user.setCreated(new Date());
        user.setUpdated(new Date());
        user.setName(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName("USER"));
        user.setRoles(roles);
        return userRepository.save(user);
    }
}

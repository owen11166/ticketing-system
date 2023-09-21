package com.owen.ticketingsystem.service;

import com.owen.ticketingsystem.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

public interface UserService {
    List<User> findAll();

    User findById(int theId);

    void save(User theUser);

    void deleteById(int theId);

    User findByUserName(String username);


}

package com.owen.ticketingsystem.service;

import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.validation.EmailNotFoundException;
import com.owen.ticketingsystem.validation.WebUser;
import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService  extends UserDetailsService {
    List<User> findAll();

    User findById(int theId);

    void save(WebUser webUser);

    void deleteById(int theId);

    User findByUserName(String userName);

    User findByEmail(String email);

    String resetPassword(String email) throws EmailNotFoundException, MessagingException;

}

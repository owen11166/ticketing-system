package com.owen.ticketingsystem.service;

import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.repository.RoleRepository;
import com.owen.ticketingsystem.repository.UserRepository;
import com.owen.ticketingsystem.validation.WebUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceimpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    public UserServiceimpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(int id) {
        User user = null;
        Optional<User> result = userRepository.findById(id);
        if (result.isPresent()) {
            user = result.get();
        }
        return user;
    }

    @Override
    public void save(WebUser webUser) {
        User user=new User();
        user.setUserName(webUser.getUserName());
        user.setPassword(passwordEncoder.encode(webUser.getPassword()));
        user.setEmail(webUser.getEmail());
        user.setRoles(Arrays.asList(roleRepository.findRoleByName("ROLE_EMPLOYEE")));

        userRepository.save(user);
    }

    @Override
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUserName(String username) {
        return userRepository.findByUserName(username);
    }


}

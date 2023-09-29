package com.owen.ticketingsystem.service;

import com.owen.ticketingsystem.entity.Role;
import com.owen.ticketingsystem.entity.User;
import com.owen.ticketingsystem.repository.RoleRepository;
import com.owen.ticketingsystem.repository.UserRepository;
import com.owen.ticketingsystem.validation.EmailNotFoundException;
import com.owen.ticketingsystem.validation.WebUser;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class UserServiceimpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EmailService emailService;

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
        User user = new User();
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

    @Override
    public User findByEmail(String email) {


        return userRepository.findByEmail(email);
    }

    @Override
    public String resetPassword(String email) throws EmailNotFoundException, MessagingException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EmailNotFoundException("找不到電子郵件" + email);
        }
        String newPassword = RandomStringUtils.randomAlphanumeric(8);
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        userRepository.save(user);

        emailService.sendNewPasswordEmail(user.getUserName(), newPassword, email);


        return newPassword;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("錯誤的使用者或密碼");
        }
        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(user.getRoles());

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                authorities);
    }

    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role tempRole : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName());
            authorities.add(tempAuthority);
        }

        return authorities;
    }
}

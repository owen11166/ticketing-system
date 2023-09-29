package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    User findByUserName(String userName);

    User findByEmail(String email);
}

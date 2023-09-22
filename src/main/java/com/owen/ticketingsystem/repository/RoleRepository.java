package com.owen.ticketingsystem.repository;

import com.owen.ticketingsystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    public Role findRoleByName(String roleName);
}

package com.marcu.vrp.backend.repository;

import com.marcu.vrp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findFirstByUsernameOrEmailOrPhone(String username, String email, String phone);
    List<User> findByEmailOrPhone(String email, String phone);

}

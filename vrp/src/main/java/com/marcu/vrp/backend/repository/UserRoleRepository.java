package com.marcu.vrp.backend.repository;

import com.marcu.vrp.backend.model.User;
import com.marcu.vrp.backend.model.UserRole;
import com.marcu.vrp.backend.model.UserRoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    UserRole findByName(@Param("name") UserRoleName userRoleName);
}

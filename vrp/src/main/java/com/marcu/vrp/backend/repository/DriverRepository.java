package com.marcu.vrp.backend.repository;

import com.marcu.vrp.backend.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long>, JpaSpecificationExecutor<Driver> {
    Driver findFirstByPhoneOrEmail(String phone, String email);
    List<Driver> findByPhoneOrEmail(String phone, String email);
}

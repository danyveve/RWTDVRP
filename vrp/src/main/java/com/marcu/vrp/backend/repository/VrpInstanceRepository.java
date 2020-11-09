package com.marcu.vrp.backend.repository;

import com.marcu.vrp.backend.model.User;
import com.marcu.vrp.backend.model.VrpInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VrpInstanceRepository extends JpaRepository<VrpInstance, Long> {
    List<VrpInstance> findAllByUserId(Long userId);
}

package com.marcu.vrp.backend.service;

import com.marcu.vrp.backend.dto.DriverDTO;

import java.util.List;

public interface DriverService {
    public List<DriverDTO> findAll();

    DriverDTO add(DriverDTO driverDTO);

    DriverDTO findById(Long id);

    DriverDTO edit(DriverDTO driverDTO, Long id);

    void deleteById(Long id);
}

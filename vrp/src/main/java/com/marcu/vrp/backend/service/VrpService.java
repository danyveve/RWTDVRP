package com.marcu.vrp.backend.service;

import com.marcu.vrp.backend.dto.RegisterUserDTO;
import com.marcu.vrp.backend.dto.StartVrpSolverRequestDTO;
import com.marcu.vrp.backend.dto.UserDTO;
import com.marcu.vrp.backend.dto.VrpInstanceDTO;

import java.security.Principal;
import java.util.List;

public interface VrpService {
    Long startVrp(StartVrpSolverRequestDTO startVrpSolverRequestDTO);

    List<VrpInstanceDTO> findAll(Principal principal);

    VrpInstanceDTO findById(Long id);

    VrpInstanceDTO stopVrp(Long id);

    void deleteById(Long id);

}

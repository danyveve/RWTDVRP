package com.marcu.vrp.backend.service;

import com.marcu.vrp.backend.dto.RegisterUserDTO;
import com.marcu.vrp.backend.dto.UserDTO;

import java.security.Principal;

public interface UserService {
    UserDTO registerUser(RegisterUserDTO registerUserDTO);
    UserDTO loadUserByUsername(String username);
    UserDTO editProfile(Principal currentUserOld, RegisterUserDTO currentUserNew);
}

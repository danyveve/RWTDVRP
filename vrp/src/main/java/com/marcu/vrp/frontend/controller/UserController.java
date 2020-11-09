package com.marcu.vrp.frontend.controller;

import com.marcu.vrp.backend.dto.RegisterUserDTO;
import com.marcu.vrp.backend.dto.UserDTO;
import com.marcu.vrp.backend.mapper.UserDTOMapper;
import com.marcu.vrp.backend.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api/user")
public class UserController {
    private UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterUserDTO registerUserDTO){
        userService.registerUser(registerUserDTO);
    }

    @GetMapping("/get/current")
    @PreAuthorize("isAuthenticated()")
    public UserDTO getCurrent(Principal principal) {
        return userService.loadUserByUsername(principal.getName());
    }

    @PostMapping("/edit/profile")
    public UserDTO editProfile(@RequestBody RegisterUserDTO registerUserDTO, Principal principal) {
        return userService.editProfile(principal, registerUserDTO);
    }
}

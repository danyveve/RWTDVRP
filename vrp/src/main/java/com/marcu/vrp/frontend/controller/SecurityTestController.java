package com.marcu.vrp.frontend.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/test")
public class SecurityTestController {
    @GetMapping("/public")
    public String publicTest() {
        return "public";
    }

    @GetMapping("/private")
    public String privateTest() { return "private"; }
}

package com.marcu.vrp.frontend.security;

import com.marcu.vrp.backend.dto.UserDTO;
import com.marcu.vrp.backend.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Component
public class UserDetailsServiceImpl implements MyUserDetailsService, ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    private final UserService userService;

    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.trace("Loading user with username {}.", username);

        UserDTO user = userService.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " not found!");
        }

        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), true, true, true, true, authorities);
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) { }
}

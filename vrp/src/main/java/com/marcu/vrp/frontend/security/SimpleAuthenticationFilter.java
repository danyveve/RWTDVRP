package com.marcu.vrp.frontend.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcu.vrp.backend.dto.UserDTO;
import com.marcu.vrp.backend.model.User;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.stream.Collectors;

public class SimpleAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String SPRING_SECURITY_FORM_DOMAIN_KEY = "domain";

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
        throws AuthenticationException {

        if (!request.getMethod()
            .equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        UsernamePasswordAuthenticationToken authRequest = null;
        try {
            authRequest = getAuthRequest(request);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
        setDetails(request, authRequest);
        return this.getAuthenticationManager()
            .authenticate(authRequest);
    }

    private UsernamePasswordAuthenticationToken getAuthRequest(HttpServletRequest request) throws IOException {
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        //we check if the credentials were sent by body
        if ((username == null || username.isEmpty())
            && (password == null || password.isEmpty())
            && "POST".equalsIgnoreCase(request.getMethod()))
        {
            ObjectMapper mapper = new ObjectMapper();
            UserDTO user = mapper.readValue(request.getInputStream(), UserDTO.class);
            username = user.getUsername();
            password = user.getPassword();
        }

        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }

        return new UsernamePasswordAuthenticationToken(username, password);
    }
}

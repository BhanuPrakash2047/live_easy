
package com.liveasy.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.liveasy.auth.dto.RegisterRequest;
import com.liveasy.auth.model.User;
import com.liveasy.auth.repository.UserRepository;
import com.liveasy.auth.util.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return jwtUtil.generateToken(user);
    }

    public void registerUser(RegisterRequest registerRequest) {
        User user = new User(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getEmail(),
                registerRequest.getRole()
        );

        userRepository.save(user);
        logger.info("User registered: {}", user.getUsername());
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}

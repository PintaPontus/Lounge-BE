package com.pinta.lounge.controller;

import com.pinta.lounge.dto.SignInCredentials;
import com.pinta.lounge.dto.SignUpCredentials;
import com.pinta.lounge.entity.UserEntity;
import com.pinta.lounge.repository.UserRepo;
import com.pinta.lounge.security.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@RequestBody SignInCredentials signIn) {
        UserEntity user = userRepo.findUser(signIn.getUsername()).orElse(null);

        if (Objects.isNull(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User Not Found");
        }

        if (!passwordEncoder.matches(signIn.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong Credentials");
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, jwtUtils.generateToken(user))
            .body(user);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody SignUpCredentials signUp) {
        UserEntity existingUser = userRepo.findUser(signUp.getUsername()).orElse(null);

        if (Objects.isNull(existingUser)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User Already Present");
        }

        UserEntity newUser = userRepo.save(new UserEntity(signUp, passwordEncoder));

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, jwtUtils.generateToken(newUser))
            .body(newUser);
    }

}

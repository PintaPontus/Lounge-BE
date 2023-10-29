package com.pinta.lounge.controller;

import com.pinta.lounge.entity.UserEntity;
import com.pinta.lounge.repository.UserRepo;
import com.pinta.lounge.security.JWTUtils;
import com.pinta.lounge.security.UserAuthService;
import com.pinta.lounge.security.UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authManager;

    @PostMapping("/signin")
    public ResponseEntity<UserEntity> signin(@RequestBody Credentials credentials){
        UserEntity user = userRepo.findUser(credentials.getUsername())
            .orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if(!passwordEncoder.matches(credentials.getPassword(), user.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, ("Bearer ").concat(jwtUtils.generateToken(user.getId())))
            .body(user);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserEntity> signup(@RequestBody Credentials credentials){
        Optional<UserEntity> existingUser = userRepo.findUser(credentials.getUsername());
        if(existingUser.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        UserEntity user = userRepo.save(
            new UserEntity()
                .setUsername(credentials.getUsername())
                .setEmail(credentials.getEmail())
                .setPassword(passwordEncoder.encode(credentials.getPassword()))
        );
        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, ("Bearer ").concat(jwtUtils.generateToken(user.getId())))
            .body(user);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(){
        return ResponseEntity.ok().build();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Credentials{
        private String username;
        private String email;
        private String password;
    }

}

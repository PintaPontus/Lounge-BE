package com.pinta.lounge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpCredentials {
    private String username;
    private String email;
    private String password;

    public String encodePassword(PasswordEncoder encoder) {
        return encoder.encode(this.password);
    }
}

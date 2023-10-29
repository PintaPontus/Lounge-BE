package com.pinta.lounge.entity;

import com.pinta.lounge.dto.SignUpCredentials;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Table(name = "user", schema = "user", catalog = "lounge")
public class UserEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    public UserEntity(SignUpCredentials signUp, PasswordEncoder encoder) {
        this.email = signUp.getEmail();
        this.username = signUp.encodePassword(encoder);
    }
}


package com.pinta.lounge.entity;

import com.pinta.lounge.dto.Credentials;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    public UserEntity(Credentials credentials, PasswordEncoder encoder) {
        this.email = credentials.getEmail();
        this.username = credentials.getUsername();
        this.password = credentials.encodePassword(encoder);
    }
}


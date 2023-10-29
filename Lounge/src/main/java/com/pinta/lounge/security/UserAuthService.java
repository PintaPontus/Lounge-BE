package com.pinta.lounge.security;

import com.pinta.lounge.entity.UserEntity;
import com.pinta.lounge.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthService implements UserDetailsService {

    @Autowired
    private UserRepo userRepository;

    @Override
    public UserPrincipal loadUserByUsername(String username) {
        return unwrapUser(userRepository.findUser(username), username);
    }

    public UserPrincipal loadById(Long id) {
        return unwrapUser(userRepository.findById(id), id.toString());
    }

    private UserPrincipal unwrapUser(Optional<UserEntity> user, String msg){
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(msg);
        }
        return new UserPrincipal(user.get());
    }
}

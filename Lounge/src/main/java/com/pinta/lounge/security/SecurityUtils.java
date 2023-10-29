package com.pinta.lounge.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static UserPrincipal getAuth() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long getId() {
        return getAuth().getId();
    }

}

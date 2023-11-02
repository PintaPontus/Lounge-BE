package com.pinta.lounge.security;

import com.pinta.lounge.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;

@Component
public class SecurityUtils {

    private static UserService userService;

    public SecurityUtils(UserService userService) {
        if (Objects.isNull(SecurityUtils.userService)) {
            SecurityUtils.userService = userService;
        }
    }

    public static UserPrincipal getAuth() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Long getId() {
        return getAuth().getId();
    }

    public static Long getIdFromSession(WebSocketSession session) {
        //noinspection DataFlowIssue
        return userService.getUserId(session.getPrincipal().getName());
    }
}

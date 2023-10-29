package com.pinta.lounge.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private AuthenticationManager authManager;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String token = extractBearerToken(request);

        if(Objects.nonNull(token)){
            Long id = jwtUtils.decodeId(token);
            UserPrincipal user = userAuthService.loadById(id);

            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
                )
            );
        }

        chain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request){
        if(Objects.isNull(request)){
            return null;
        }
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(Objects.isNull(authorization) || (!authorization.startsWith("Bearer "))){
            return null;
        }
        return authorization.substring(authorization.indexOf(" ")+1);
    }
}

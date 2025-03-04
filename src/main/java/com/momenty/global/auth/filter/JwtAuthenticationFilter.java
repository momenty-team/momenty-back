package com.momenty.global.auth.filter;

import com.momenty.global.annotation.UserId;
import com.momenty.global.auth.jwt.JwtTokenProvider;
import com.momenty.global.exception.AuthenticationException;
import com.momenty.global.exception.InvalidJwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private HandlerMappingIntrospector handlerMappingIntrospector;

    @Override
    protected void doFilterInternal (
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        boolean requiresJwt = false;
        HandlerExecutionChain handlerChainExec = findHandler(request);

        if (handlerChainExec != null && handlerChainExec.getHandler() instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handlerChainExec.getHandler();
            requiresJwt = Arrays.stream(handlerMethod.getMethodParameters())
                    .anyMatch(param -> param.hasParameterAnnotation(UserId.class));

            if (!requiresJwt) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String accessToken = extractTokenFromCookie(request, "access_token");

        if (accessToken != null && tokenProvider.validateToken(accessToken)) {
            // Access token이 유효한 경우
            String userId = tokenProvider.getUserIdFromToken(accessToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            throw new InvalidJwtTokenException();
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private HandlerExecutionChain findHandler(HttpServletRequest request) {
        try {
            for (HandlerMapping mapping : handlerMappingIntrospector.getHandlerMappings()) {
                HandlerExecutionChain chain = mapping.getHandler(request);
                if (chain != null) {
                    return chain;
                }
            }
        } catch (Exception e) {
            throw new AuthenticationException();
        }
        return null;
    }
}

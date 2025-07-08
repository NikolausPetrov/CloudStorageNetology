package ru.netology.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.netology.dto.AuthRequest;
import ru.netology.security.JwtTokenUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    public AuthService(AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public String loginUser(AuthRequest authRequest) {
        logger.debug("Начало процесса аутентификации для пользователя: {}", authRequest.getLogin());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtils.generateToken(authentication);
            tokenStore.put(token, authRequest.getLogin());
            
            logger.debug("Токен успешно сгенерирован для пользователя: {}", authRequest.getLogin());
            return token;
        } catch (AuthenticationException ex) {
            logger.error("Ошибка аутентификации для пользователя {}: {}", authRequest.getLogin(), ex.getMessage());
            throw new BadCredentialsException("Ошибка авторизации: " + ex.getMessage());
        }
    }

    public void logoutUser(String authToken) {
        String username = tokenStore.get(authToken);
        tokenStore.remove(authToken);
        logger.debug("Пользователь {} вышел из системы, токен удален", username);
    }
}
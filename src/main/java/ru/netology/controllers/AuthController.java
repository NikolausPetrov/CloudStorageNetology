package ru.netology.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.dto.AuthRequest;
import ru.netology.dto.AuthResponse;
import ru.netology.services.AuthService;

@RestController
@RequestMapping("/")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * @param authRequest 
     * @return auth-token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        logger.info("Попытка входа пользователя: {}", authRequest.getLogin());
        
        String token = authService.loginUser(authRequest);
        if (token == null) {
            logger.warn("Неудачная попытка входа для пользователя: {}", authRequest.getLogin());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        logger.info("Успешный вход пользователя: {}", authRequest.getLogin());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * @param token 
     * @return 200 OK
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("auth-token") String token) {
        logger.info("Попытка выхода пользователя");
        authService.logoutUser(token);
        logger.info("Пользователь успешно вышел из системы");
        return ResponseEntity.ok().build();
    }
}
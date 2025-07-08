package ru.netology.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.netology.entities.User;
import ru.netology.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    private final String USERNAME = "admin";
    private final String PASSWORD = "admin";

    @Test
    void loadUserByUsernameReturnsUserDetails() {
        User user = new User(USERNAME, PASSWORD, "ROLE_USER");
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername(USERNAME);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());
    }

    @Test
    void loadUserByUsernameThrowsWhenNotFound() {
        given(userRepository.findByUsername(USERNAME)).willReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(USERNAME));
    }
}
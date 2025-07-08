package ru.netology.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.netology.security.JwtEntryPoint;
import ru.netology.security.JwtTokenFilter;
import ru.netology.services.UserDetailsServiceImpl;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtEntryPoint jwtEntryPoint;
    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtEntryPoint jwtEntryPoint, JwtTokenFilter jwtTokenFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtEntryPoint = jwtEntryPoint;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests()
                .antMatchers("/login", "/logout").permitAll()
                .anyRequest().authenticated()
            .and()
            .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(jwtEntryPoint)
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:8081"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
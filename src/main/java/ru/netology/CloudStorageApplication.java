package ru.netology;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.entities.User;
import ru.netology.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class CloudStorageApplication {

    private static final Logger logger = LoggerFactory.getLogger(CloudStorageApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CloudStorageApplication.class, args);
        logger.info("Облачное хранилище запущено: http://localhost:9090");
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (!users.findByUsername("user").isPresent()) {
                users.save(new User("user", encoder.encode("password"), "ROLE_USER"));
                logger.info("Пользователь 'user' создан с паролем 'password'");
            } else {
                logger.info("Пользователь 'user' уже существует");
            }
        };
    }
}
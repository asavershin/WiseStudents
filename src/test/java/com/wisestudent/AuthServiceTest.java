package com.wisestudent;

import com.wisestudent.config.DBConfig;
import com.wisestudent.config.StorageConfig;
import com.wisestudent.models.Role;
import com.wisestudent.models.UserEntity;
import com.wisestudent.repositories.UserRepository;
import com.wisestudent.services.AuthService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AuthServiceTest extends AbstractTest{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @BeforeEach
    @AfterEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    @SneakyThrows
    public void registrationTest() {
        // Given
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").build();

        // When
        var responseUser = authService.register(user);

        //Then
        var savedUser = userRepository.findById(responseUser.getId()).orElseThrow();

        assertEquals(responseUser.getId(), savedUser.getId());

        assertEquals(user.getName(), responseUser.getName());
        assertEquals(user.getName(), savedUser.getName());

        assertEquals(user.getLogin(), responseUser.getLogin());
        assertEquals(user.getLogin(), savedUser.getLogin());

        assertEquals(user.getPassword(), savedUser.getPassword());

        assertEquals(Role.ROLE_USER, responseUser.getRole());
        assertEquals(Role.ROLE_USER, savedUser.getRole());
    }
}


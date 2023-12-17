package com.wisestudent.services;

import com.wisestudent.exceptions.AccessDeniedException;
import com.wisestudent.exceptions.DuplicateException;
import com.wisestudent.models.Role;
import com.wisestudent.models.UserEntity;
import com.wisestudent.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public UserEntity register(UserEntity user) {
        UserEntity userFromDB = userRepository.findByLogin(user.getLogin());

        if (userFromDB != null) {
            throw new DuplicateException("Пользователь с таким логином " + user.getLogin() + " уже есть", "login");
        }

        user.setRole(Role.ROLE_USER);
        user.setPassword(user.getPassword());

        return userRepository.save(user);
    }

    public UserEntity login(String login, String password) {
        var userFromDB = userRepository.findByLogin(login);

        if (userFromDB == null || !userFromDB.getPassword().equals(password)) {
            throw new EntityNotFoundException("Пользователь с таким логином или паролем не найден");
        }

        if(userFromDB.getBannedUntil() != null && userFromDB.getBannedUntil().isAfter(LocalDate.now())){
            throw new AccessDeniedException("Вам запрещён вход до: " + userFromDB.getBannedUntil());
        }

        return userFromDB;
    }


}

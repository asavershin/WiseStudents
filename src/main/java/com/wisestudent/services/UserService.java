package com.wisestudent.services;

import com.wisestudent.models.Role;
import com.wisestudent.models.UserEntity;
import com.wisestudent.repositories.UserRepository;
import com.wisestudent.security.AppUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;


    public Page<UserEntity> getUsers(int page) {
        var pageable = PageRequest.of(page, 20, Sort.by(
                Sort.Order.asc("id")
        ));

        return userRepository.findAll(pageable);

    }

    public void banUserByAdmin(Long id, LocalDate date) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Пользователем не найден с id: " + id));
        user.setBannedUntil(date);
        userRepository.save(user);
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByLogin(username);
    }

    public UserEntity changeRole(String login, Role role) {
        var user = this.userRepository.findByLogin(login);
        user.setRole(role);
        this.userRepository.save(user);
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new AppUserDetails(userRepository.findByLogin(username));
    }
}

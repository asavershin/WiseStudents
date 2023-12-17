package com.wisestudent.controllers;

import com.wisestudent.dto.PageResponse;
import com.wisestudent.dto.user.ResponseUser;
import com.wisestudent.mappers.UserMapper;
import com.wisestudent.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/wise-students/user")
public class UserController {

    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping()
    @Operation(description = "Получить всех пользователей")
    public PageResponse<ResponseUser> getUsers(@RequestParam int page) {
        return userMapper.pageUsersToPageResponse(
                userService.getUsers(page)
        );
    }

}

package com.wisestudent.controllers;

import com.wisestudent.dto.admin.RequestBanUser;
import com.wisestudent.dto.admin.RequestUpdateJob;
import com.wisestudent.dto.comment.ResponseComment;
import com.wisestudent.dto.user.RequestLoginUser;
import com.wisestudent.dto.user.ResponseUser;
import com.wisestudent.mappers.UserMapper;
import com.wisestudent.services.CommentService;
import com.wisestudent.services.CommentsCleanupJobService;
import com.wisestudent.services.PostService;
import com.wisestudent.services.SubjectService;
import com.wisestudent.services.UserService;
import com.wisestudent.dto.news.RequestNews;
import com.wisestudent.dto.news.ResponseNewsWithoutFiles;
import com.wisestudent.mappers.NewsMapper;
import com.wisestudent.models.Role;
import com.wisestudent.services.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.quartz.CronExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

//@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/wise-students/admin")
public class AdminController {
    private final CommentService commentService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final PostService postService;
    private final SubjectService subjectService;
    private final NewsMapper newsMapper;
    private final NewsService newsService;

    private final CommentsCleanupJobService commentsCleanupJobService;

    @PostMapping("/user/ban")
    @Operation(description = "Забанить пользователя на определённый срок")
    public void banUserByAdmin(@Valid @RequestBody RequestBanUser request) {
        userService.banUserByAdmin(request.getUserId(), LocalDate.parse(request.getDate()));
    }

    @DeleteMapping("/post/{postId}")
    @Operation(description = "Удалить пост, все его файлы и комментарии с их файлами")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long postId) {
        postService.deletePostByAdmin(postId);
    }

    @DeleteMapping("/subject/{subjectId}")
    @Operation(description = "Удалить предмет, все его посты, файлы и комментарии с их файлами")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubject(@PathVariable Long subjectId) {
        subjectService.deleteSubjectByAdmin(subjectId);
    }

    @PostMapping("/change-job")
    @ResponseStatus(HttpStatus.OK)
    public String changeJobSchedule(@Valid @RequestBody RequestUpdateJob request) {
        commentsCleanupJobService.updateJobSchedule(request);
        return "Job schedule updated successfully";
    }


    @PostMapping("/add-moderator")
    @Operation(description = "Add moderator role")
    public ResponseUser addModerator(@Valid @RequestBody RequestLoginUser requestUser) {
        return userMapper.userToResponseUser(
                this.userService.changeRole(
                        requestUser.getLogin(),
                        Role.ROLE_MODERATOR
                )
        );
    }

}


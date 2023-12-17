package com.wisestudent.controllers;

import com.wisestudent.dto.RequestDeleteFiles;
import com.wisestudent.dto.post.PostFilter;
import com.wisestudent.dto.post.RequestPost;
import com.wisestudent.dto.post.ResponsePost;
import com.wisestudent.dto.post.ResponsePostWithoutFiles;
import com.wisestudent.mappers.PostMapper;
import com.wisestudent.services.PostService;
import com.wisestudent.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//@CrossOrigin
@RestController
@RequestMapping("/wise-students/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;
    private final PostMapper postMapper;

    @GetMapping
    @Operation(description = "Фильтр постов")
    public List<ResponsePost> filterPosts(@Valid PostFilter request) {
        return postService.filterPosts(request.getPageNumber(), request.getPageSize(), request);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(description = "Создание поста с файлами")
    public ResponsePostWithoutFiles createPostWithFiles(
            @RequestParam("name") String name,
            @RequestParam("text") String text,
            @RequestParam("subjectId") long subjectId,
            @RequestParam("year") Integer year,
            @RequestParam("semester") Integer semester,
            @RequestParam("postTypeId") long postTypeId,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var userId = userService.getUserByUsername(userDetails.getUsername()).getId();

        RequestPost request = RequestPost.builder()
                .name(name)
                .text(text)
                .subjectId(subjectId)
                .year(year)
                .semester(semester)
                .postTypeId(postTypeId)
                .userId(userId)
                .build();

        return postService.createPostWithFiles(
                postMapper.requestPostToPost(request),
                postTypeId,
                subjectId,
                userId,
                files
        );

    }

    @DeleteMapping
    @Operation(description = "Удаление файлов у поста")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFiles(@Valid @RequestBody RequestDeleteFiles request) {
        postService.deleteFiles(request.getUserId(), request.getId(), request.getFilesids());
    }

}

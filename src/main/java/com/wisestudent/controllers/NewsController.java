package com.wisestudent.controllers;

import com.wisestudent.dto.PageDto;
import com.wisestudent.dto.comment.RequestGetThreadComments;
import com.wisestudent.dto.comment.RequestGetThreads;
import com.wisestudent.dto.comment.ResponseComment;
import com.wisestudent.dto.news.RequestNews;
import com.wisestudent.dto.news.ResponseNews;
import com.wisestudent.dto.news.ResponseNewsWithoutFiles;
import com.wisestudent.mappers.NewsMapper;
import com.wisestudent.mappers.UserMapper;
import com.wisestudent.services.NewsService;
import com.wisestudent.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wise-students/news")
public class NewsController {
    private final NewsService newsService;
    private final NewsMapper newsMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    @Operation(description = "Получить новости")
    public List<ResponseNews> getNews(@Valid PageDto pageDto) {
        return newsService.getNews(PageRequest.of(pageDto.getPageNumber(), pageDto.getPageSize()));
    }

    @GetMapping("/threads")
    @Operation(description = "Получить треды новостей")
    public List<ResponseComment> getThreads(@Valid RequestGetThreads request) {
        return newsService.getThreads(request.getNewsId(),
                request.getPageSize(),
                request.getPageNumber()
        );
    }

    @GetMapping("/threads/comments")
    @Operation(description = "Получить комментарии под тредом")
    public List<ResponseComment> getThreadComments(@Valid RequestGetThreadComments request) {
        return newsService.getThreadComments(request.getThreadId(), request.getPageSize(), request.getPageNumber());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(description = "Создание новости с файлами")
    public ResponseNewsWithoutFiles createNews(
            @RequestParam("name") String name,
            @RequestParam("text") String text,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userId = userService.getUserByUsername(userDetails.getUsername()).getId();

        var request = new RequestNews(name, text);

        return newsMapper.newsToResponseNewsWithoutFiles(
                newsService.createNewsWithFiles(
                        newsMapper.requestNewsToNews(request),
                        userId,
                        files
                )
        );
    }

}

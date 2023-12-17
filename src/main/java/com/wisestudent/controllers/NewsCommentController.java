package com.wisestudent.controllers;

import com.wisestudent.dto.comment.*;
import com.wisestudent.mappers.CommentMapper;
import com.wisestudent.services.NewsCommentService;
import com.wisestudent.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/wise-students/news/thread")
public class NewsCommentController {

    private final NewsCommentService newsCommentService;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseCommentWithoutFiles createThread(
            //@Valid @RequestPart RequestPostComment request,
            @RequestParam("newsId") long newsId,
            @RequestParam("text") String text,
            @RequestParam("is_anonymous") String isAnonymous,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var userId = userService.getUserByUsername(userDetails.getUsername()).getId();

        var request = RequestThread.builder()
                .newsId(newsId)
                .text(text)
                .isAnonymous(isAnonymous)
                .build();

        return commentMapper.commentWithoutFilesToResponseComment(
                newsCommentService.createThread(
                request.getNewsId(),
                userId,
                commentMapper.requestToThread(request),
                files
        ));
    }

    @PostMapping(path = "/comment",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseCommentWithoutFiles createThreadComment(
            //@Valid @RequestPart RequestPostComment request,
            @RequestParam("threadId") long threadId,
            @RequestParam("text") String text,
            @RequestParam("is_anonymous") String isAnonymous,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var userId = userService.getUserByUsername(userDetails.getUsername()).getId();

        var request = RequestThreadComment.builder()
                .threadId(threadId)
                .text(text)
                .isAnonymous(isAnonymous)
                .build();

        return commentMapper.commentWithoutFilesToResponseComment(
                newsCommentService.createThreadComment(
                        request.getThreadId(),
                        userId,
                        commentMapper.requestToThreadComment(request),
                        files
                ));
    }
}

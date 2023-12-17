package com.wisestudent.controllers;

import com.wisestudent.dto.PageResponse;
import com.wisestudent.dto.RequestDeleteFiles;
import com.wisestudent.dto.admin.RequestBanUser;
import com.wisestudent.dto.comment.RequestGetComments;
import com.wisestudent.dto.comment.RequestPostComment;
import com.wisestudent.dto.comment.ResponseComment;
import com.wisestudent.dto.comment.ResponseCommentWithoutFiles;
import com.wisestudent.mappers.CommentMapper;
import com.wisestudent.services.CommentService;
import com.wisestudent.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

//@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/wise-students/post/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserService userService;

    @GetMapping("/moder/{commentId}")
    @Operation(description = "Получить всю информацию о любом кмментарии с пользователем")
    public ResponseComment getInfoAboutCommentForAdmin(@PathVariable Long commentId) {
        return commentService.getInfoAboutCommentForAdmin(commentId);
    }


    @DeleteMapping("/moder/{commentId}")
    @Operation(description = "Удалить комментарий и все его файлы")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseCommentWithoutFiles createComment(
            //@Valid @RequestPart RequestPostComment request,
            @RequestParam("post_id") long postId,
            @RequestParam("text") String text,
            @RequestParam("is_anonymous") String isAnonymous,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var userId = userService.getUserByUsername(userDetails.getUsername()).getId();

        RequestPostComment request = RequestPostComment.builder()
                .postId(postId)
                .text(text)
                .isAnonymous(isAnonymous)
                .build();

        return commentMapper.commentWithoutFilesToResponseComment(
                commentService.createComment(
                        request.getPostId(),
                        userId,
                        commentMapper.requestToComment(request),
                        files
                )
        );

    }

    @GetMapping
    public PageResponse<ResponseComment> getComments(@Valid RequestGetComments request) {
        return commentService.getCommentsByPost(request.getPostId(),
                request.getPageSize(),
                request.getPageNumber()
        );
    }

    @DeleteMapping
    @Operation(description = "Удаление файлов у комментария")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFiles(@Valid @RequestBody RequestDeleteFiles request) {
        commentService.deleteFiles(request.getUserId(), request.getId(), request.getFilesids());
    }

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(description = "Обновить любой комментарий")
    public ResponseCommentWithoutFiles updateAnyComment(
            @RequestParam("commentId") Long commentId,
            @RequestParam("text") @Valid @NotNull String text,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var userId = userService.getUserByUsername(userDetails.getUsername()).getId();
        return commentMapper.commentWithoutFilesToResponseComment(
                commentService.updateComment(commentId, userId, text, files));
    }
}

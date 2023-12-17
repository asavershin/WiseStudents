package com.wisestudent.services;

import com.wisestudent.dto.PageResponse;
import com.wisestudent.dto.comment.ResponseComment;
import com.wisestudent.exceptions.AccessDeniedException;
import com.wisestudent.exceptions.DeleteFileException;
import com.wisestudent.mappers.CommentMapper;
import com.wisestudent.models.*;
import com.wisestudent.models.posts.PostCommentEntity;
import com.wisestudent.models.posts.PostCommentFileEntity;
import com.wisestudent.repositories.*;
import com.wisestudent.repositories.posts.PostCommentFileRepository;
import com.wisestudent.repositories.posts.PostCommentRepository;
import com.wisestudent.repositories.posts.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostCommentRepository postCommentRepository;
    private final CommentMapper commentMapper;
    private final FileService fileService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostCommentFileRepository commentFileRepository;
    private final DefaultCommentRepository defaultCommentRepository;
    private final DefaultFileRepository defaultFileRepository;
    @Transactional
    public Comment createComment(Long postId, Long userId, PostCommentEntity comment, List<MultipartFile> files) {
        var post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Пост с id " + postId + " не существует")
        );
        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId + " не существует")
        );
        post.getPostComments().add(comment);
        comment.setPost(post);
        comment.setUser(user);
        comment.setFiles(new HashSet<>());
        comment = postCommentRepository.save(comment);

        if(files != null) {
            var commentFiles = fileService.saveFiles(files, PostCommentFileEntity.class);
            for(var file : commentFiles){
                file.getComments().add(comment);
            }
            comment.getFiles().addAll(commentFiles);
        }

        return postCommentRepository.saveAndFlush(comment);
    }

    public PageResponse<ResponseComment> getCommentsByPost(Long postId, Integer pageSize, Integer pageNumber) {
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.asc("id")
        ));

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Пост с id " + postId + " не существует");
        }

        var comments = postCommentRepository.findAllByPostId(postId, pageable);
        comments.forEach(c -> c.setUser(c.getIsAnonymous()?null:c.getUser()));

        return commentMapper.pageCommentsToPageResponse(
                comments,
                comments.stream().map(comment -> fileService.getFilesData(
                    comment.getFiles()
                )).toList()
        );
    }

    public void deleteFiles(Long userId, Long commentId, List<Long> filesIds){
        var user = userRepository.findById(userId)
                .orElseThrow(()->new EntityNotFoundException("Пользоватеоя с id "+userId+" не найдено"));
        var comment = defaultCommentRepository.findCommentByIdWithFilesAndUser(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с id " + commentId + " не существует."));
        if(!userId.equals(comment.getUser().getId()) && user.getRole() != Role.ROLE_ADMIN){
            throw new AccessDeniedException("Access denied exception: вам отказано в доступе в удалении этого контента.");
        }
        if (filesIds.size() > comment.getFiles().size()){
            throw new DeleteFileException("Количество файлов больше фактического.");
        }
        var files = new HashSet<DefaultFileEntity>();
        var commentFilesId = comment.getFiles().stream().map(File::getId).toList();
        if (!commentFilesId.containsAll(filesIds)){
            throw new DeleteFileException("Комментарий не содержит указанных файлов");
        }
        comment.getFiles().forEach(file ->{
                    if (filesIds.contains(file.getId())){
                        files.add(file);
                    }
                }
        );
        comment.getFiles().removeAll(files);
        files.forEach(file -> file.setComment(null));
        defaultFileRepository.deleteAllInBatch(files);
        fileService.deleteFiles(files);
    }

    public ResponseComment getInfoAboutCommentForAdmin(Long id){
        var comment = defaultCommentRepository.findCommentByIdWithFilesAndUser(id)
                .orElseThrow(()-> new EntityNotFoundException("Комментарий не найден с id: " + id));
        var filesDTO = fileService.getFilesData(comment.getFiles());
        return commentMapper.mapToResponseComment(comment, filesDTO);
    }

    @Transactional
    public void deleteCommentByAdmin(Long id){
        var comment = defaultCommentRepository.findCommentByIdWithFilesAndUserAndPost(id)
                .orElseThrow(()-> new EntityNotFoundException("Комментарий не найден с id: " + id));
        var files = comment.getFiles();
        try {
            defaultCommentRepository.delete(comment);
        }catch (Exception e){
            throw new RuntimeException("Неизвестная ошибка");
        }
        fileService.deleteFiles(files);
    }

    @Transactional
    public Comment updateComment(Long commentId, Long userId, String text, List<MultipartFile> files){
        userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId + " не существует")
        );
        var comment = defaultCommentRepository.findById(commentId).orElseThrow(
                () -> new AccessDeniedException("Вы не можете править чужой комментарий")
        );
        comment.setText(text);

        if(files != null) {
            var commentFiles = fileService.saveFiles(files, DefaultFileEntity.class);
            for(var file : commentFiles){
                file.setComment(comment);
            }
            comment.getFiles().addAll(commentFiles);
        }

        return defaultCommentRepository.saveAndFlush(comment);
    }

    @Transactional
    public void deleteBatch(List<DefaultCommentEntity> comments) {
        comments.forEach(comment -> {
            fileService.deleteFiles(comment.getFiles());
            var files = comment.getFiles();
            comment.setFiles(null);
            defaultFileRepository.deleteAll(files);
        });
        defaultCommentRepository.deleteAll(comments);
    }
}

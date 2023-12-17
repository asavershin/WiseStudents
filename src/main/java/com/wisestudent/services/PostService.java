package com.wisestudent.services;

import com.wisestudent.dto.post.PostFilter;
import com.wisestudent.dto.post.ResponsePost;
import com.wisestudent.dto.post.ResponsePostWithoutFiles;
import com.wisestudent.exceptions.AccessDeniedException;
import com.wisestudent.exceptions.DeleteFileException;
import com.wisestudent.mappers.PostMapper;
import com.wisestudent.models.*;
import com.wisestudent.models.posts.PostCommentFileEntity;
import com.wisestudent.models.posts.PostEntity;
import com.wisestudent.models.posts.PostFileEntity;
import com.wisestudent.repositories.*;
import com.wisestudent.repositories.posts.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final SubjectRepository subjectRepository;
    private final PostTypeRepository postTypeRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final PostMapper postMapper;
    private final PostFileRepository postFileRepository;
    private final PostCommentFileRepository commentFileRepository;
    private final PostQuerdslRepository postQuerdslRepository;
    private final CommentService commentService;
    private final PostCommentRepository commentRepository;

    public List<ResponsePost> filterPosts(int pageNumber, int pageSize, @NotNull PostFilter filter) {
        var posts = postQuerdslRepository.postFilterByYearSubjectPostType(pageNumber, pageSize, filter);
        return posts.stream()
                .map(post -> postMapper.postWithFilesToResponsePost(
                                post,
                                fileService.getFilesData(
                                        post.getFiles()
                                )
                        )
                ).toList();
    }

    @Transactional
    public ResponsePostWithoutFiles createPostWithFiles(PostEntity post, Long postTypeId, Long subjectId, Long userId, List<MultipartFile> files) {
        var type = postTypeRepository.findById(postTypeId).orElseThrow(
                () -> new EntityNotFoundException("Тип поста с id " + postTypeId + " не существует")
        );
        var subject = subjectRepository.findById(subjectId).orElseThrow(
                () -> new EntityNotFoundException("Предмет с id " + subjectId + " не существует")
        );
        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId + " не существует")
        );
        post.setPostType(type);
        type.getPosts().add(post);
        post.setSubject(subject);
        subject.getPosts().add(post);
        post.setUser(user);
//        user.getPosts().add(post);
        post.setFiles(new HashSet<>());
        post = postRepository.save(post);

        if (files != null) {
            var postFiles = fileService.saveFiles(files, PostFileEntity.class);
            for (var file : postFiles) {
                file.getPosts().add(post);
            }
            post.getFiles().addAll(postFiles);
        }

        post = postRepository.saveAndFlush(post);

        return postMapper.postWithFilesToResponsePost(post);
    }

    public void deleteFiles(Long userId, Long postId, List<Long> filesIds){
        var user = userRepository.findById(userId)
                .orElseThrow(()->new EntityNotFoundException("Пользоватеоя с id "+userId+" не найдено"));
        var post = postRepository.findPostByIdWithFiles(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с id " + postId + " не существует."));
        if(!userId.equals(post.getUser().getId()) && user.getRole() != Role.ROLE_ADMIN){
            throw new AccessDeniedException("Access denied exception: вам отказано в доступе в удалении этого контента.");
        }
        if (filesIds.size() > post.getFiles().size()){
            throw new DeleteFileException("Количество файлов больше фактического.");
        }
        var files = new HashSet<PostFileEntity>();
        var postFilesId = post.getFiles().stream().map(PostFileEntity::getId).toList();
        if (!postFilesId.containsAll(filesIds)){
            throw new DeleteFileException("Пост не содержит указанных файлов");
        }
        post.getFiles().forEach(file ->{
                    if (filesIds.contains(file.getId())){
                        files.add(file);
                    }
                }
        );
        post.getFiles().removeAll(files);
//        files.forEach(file -> file.setPosts(null));
        postFileRepository.deleteAllInBatch(files);
        fileService.deleteFiles(files);
    }

    @Transactional
    public void deletePostByAdmin(Long id){
        var post = postRepository.findPostByIdWithFilesAndUserAndComments(id)
                .orElseThrow(()-> new EntityNotFoundException("Пост не найден с id: " + id));
        var commentFiles = new HashSet<PostCommentFileEntity>();
        var postFiles = post.getFiles();
        post.getPostComments().forEach(c->commentFiles.addAll(c.getFiles()));
        try {
            postFileRepository.deleteAllInBatch(postFiles);
            commentFileRepository.deleteAllInBatch(commentFiles);
            commentRepository.deleteAllInBatch(post.getPostComments());
            postRepository.deleteAllInBatch(Collections.singletonList(post));
        }catch (Exception e){
            throw new RuntimeException("Неизвестная ошибка");
        }
        fileService.deleteFiles(postFiles);
        fileService.deleteFiles(commentFiles);
    }
}

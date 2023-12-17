package com.wisestudent.services;

import com.wisestudent.mappers.CommentMapper;
import com.wisestudent.models.news.ThreadCommentEntity;
import com.wisestudent.models.news.ThreadCommentFileEntity;
import com.wisestudent.models.news.ThreadEntity;
import com.wisestudent.models.news.ThreadFileEntity;
import com.wisestudent.repositories.UserRepository;
import com.wisestudent.repositories.news.NewsRepository;
import com.wisestudent.repositories.news.ThreadCommentRepository;
import com.wisestudent.repositories.news.ThreadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsCommentService {
    private final NewsRepository newsRepository;
    private final ThreadRepository threadRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final CommentMapper commentMapper;
    private final ThreadCommentRepository threadCommentRepository;

    @Transactional
    public ThreadEntity createThread(Long newsId, Long userId, ThreadEntity thread, List<MultipartFile> files) {
        var news = newsRepository.findById(newsId).orElseThrow(
                () -> new EntityNotFoundException("Новость с id " + newsId + " не существует")
        );
        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId + " не существует")
        );
        news.getThreads().add(thread);
        thread.setNews(news);
        thread.setUser(user);
        thread.setFiles(new HashSet<>());
        thread = threadRepository.save(thread);

        if(files != null) {
            var commentFiles = fileService.saveFiles(files, ThreadFileEntity.class);
            for(var file : commentFiles){
                file.setThread(thread);
            }
            thread.getFiles().addAll(commentFiles);
        }

        thread = threadRepository.saveAndFlush(thread);
        return thread;
    }

    @Transactional
    public ThreadCommentEntity createThreadComment(Long threadId, Long userId, ThreadCommentEntity threadComment, List<MultipartFile> files) {
        var thread = threadRepository.findById(threadId).orElseThrow(
                () -> new EntityNotFoundException("Тред с id " + threadId + " не существует")
        );
        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId + " не существует")
        );
        thread.getChildComments().add(threadComment);
        threadComment.setParentComment(thread);
        threadComment.setUser(user);
        threadComment.setFiles(new HashSet<>());
        threadComment = threadCommentRepository.save(threadComment);

        if(files != null) {
            var commentFiles = fileService.saveFiles(files, ThreadCommentFileEntity.class);
            for(var file : commentFiles){
                file.setThreadComment(threadComment);
            }
            threadComment.getFiles().addAll(commentFiles);
        }

        threadComment = threadCommentRepository.saveAndFlush(threadComment);
        return threadComment;
    }
}

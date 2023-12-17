package com.wisestudent.services;

import com.wisestudent.dto.comment.ResponseComment;
import com.wisestudent.dto.news.ResponseNews;
import com.wisestudent.mappers.CommentMapper;
import com.wisestudent.mappers.NewsMapper;
import com.wisestudent.models.news.NewsEntity;
import com.wisestudent.models.news.NewsFileEntity;
import com.wisestudent.repositories.UserRepository;
import com.wisestudent.repositories.news.NewsRepository;
import com.wisestudent.repositories.news.ThreadCommentRepository;
import com.wisestudent.repositories.news.ThreadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {
    private final NewsRepository newsRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final NewsMapper newsMapper;
    private final ThreadRepository threadRepository;
    private final CommentMapper commentMapper;
    private final ThreadCommentRepository threadCommentRepository;
    @Transactional
    public NewsEntity createNewsWithFiles(NewsEntity news, Long userId, List<MultipartFile> files) {
        var user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId + " не существует")
        );
        news.setUser(user);
        news.setFiles(new HashSet<>());
        news = newsRepository.save(news);

        if (files != null) {
            var postFiles = fileService.saveFiles(files, NewsFileEntity.class);
            for (var file : postFiles) {
                file.setNews(news);
            }
            news.getFiles().addAll(postFiles);
        }

        news = newsRepository.saveAndFlush(news);

        return news;
    }

    public List<ResponseNews> getNews(Pageable pageable) {
        var news = newsRepository.findAll(pageable);
        return news.stream()
                .map(n -> newsMapper.newsWithFilesToResponsePost(
                                n,
                                fileService.getFilesData(
                                        n.getFiles()
                                )
                        )
                ).toList();
    }

    public List<ResponseComment> getThreads(Long newsId, Integer pageSize, Integer pageNumber) {
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.asc("id")
        ));

        if (!newsRepository.existsById(newsId)) {
            throw new EntityNotFoundException("Новость с id " + newsId + " не существует");
        }
        var sliceThreads = threadRepository.findThreadsWithChildCommentsCountByNewsId(newsId, pageable);
        return sliceThreads.stream()
                .map(post -> commentMapper.mapToResponseComment(
                                post,
                                fileService.getFilesData(
                                        post.getFiles()
                                )
                        )
                ).toList();
    }

    public List<ResponseComment> getThreadComments(Long threadId, Integer pageSize, Integer pageNumber) {
        var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.asc("id")
        ));

        if (!threadRepository.existsById(threadId)) {
            throw new EntityNotFoundException("Тред с id " + threadId + " не существует");
        }

        var sliceThreadsComments = threadCommentRepository.findAllByParentCommentId(threadId, pageable);
        return sliceThreadsComments.stream()
                .map(post -> commentMapper.mapToResponseComment(
                                post,
                                fileService.getFilesData(
                                        post.getFiles()
                                )
                        )
                ).toList();
    }
}

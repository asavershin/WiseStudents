package com.wisestudent;

import com.wisestudent.models.Role;
import com.wisestudent.models.UserEntity;
import com.wisestudent.models.news.NewsEntity;
import com.wisestudent.models.news.ThreadCommentEntity;
import com.wisestudent.models.news.ThreadEntity;
import com.wisestudent.repositories.UserRepository;
import com.wisestudent.repositories.news.*;
import com.wisestudent.services.NewsCommentService;
import com.wisestudent.services.NewsService;
import com.wisestudent.utils.FilesCreator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NewsServiceTest extends AbstractTest{
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FilesCreator filesCreator;
    @Autowired
    private NewsService newsService;
    @Autowired
    private NewsFilesRepository newsFilesRepository;
    @Autowired
    private NewsCommentService newsCommentService;
    @Autowired
    private ThreadRepository threadRepository;
    @Autowired
    private ThreadFileRepository threadFileRepository;
    @Autowired
    private ThreadCommentRepository threadCommentRepository;

    @BeforeEach
    @AfterEach
    @Transactional
    public void clear() {
        newsRepository.deleteAll();
        threadRepository.deleteAll();
        userRepository.deleteAll();
        newsFilesRepository.deleteAll();
    }

    @ValueSource(strings = {"jpg"})
    @ParameterizedTest
    @Transactional
    @SneakyThrows
    public void newsCreateTest(String fileType) {
        // Given
        MultipartFile testFile1 = filesCreator.createTestFile(fileType, "test");
        MultipartFile testFile2 = filesCreator.createTestFile(fileType, "test");
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").role(Role.ROLE_USER).build();
        user = userRepository.save(user);
        var news = NewsEntity.builder().threads(new ArrayList<>()).name("post").text("text").user(user).build();

        // When
        var savedNews = newsService.createNewsWithFiles(news, user.getId(), List.of(testFile1, testFile2));
        var file = savedNews.getFiles().stream().toList().get(0);

        //Then
        assertNotNull(savedNews.getId());
        assertNotNull(savedNews);
        assertNotNull(file);
        assertEquals(newsRepository.findAll().size(), 1);
        assertEquals(newsFilesRepository.findAll().size(), 2);
        assertEquals(savedNews.getName(), news.getName());
        assertEquals(savedNews.getText(), news.getText());
    }

    @ValueSource(strings = {"jpg"})
    @ParameterizedTest
    @Transactional
    @SneakyThrows
    public void createThreadTest(String fileType) {
        // Given
        MultipartFile testFile1 = filesCreator.createTestFile(fileType, "test");
        MultipartFile testFile2 = filesCreator.createTestFile(fileType, "test");
        MultipartFile testFile3 = filesCreator.createTestFile(fileType, "test");
        MultipartFile testFile4 = filesCreator.createTestFile(fileType, "test");
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").role(Role.ROLE_USER).build();
        user = userRepository.save(user);
        var news = NewsEntity.builder().threads(new ArrayList<>()).name("post").text("text").user(user).build();
        var thread = ThreadEntity.builder().isAnonymous(false).text("Kapibara").build();
        var savedNews = newsService.createNewsWithFiles(news, user.getId(), List.of(testFile1, testFile2));

        // When
        var savedThread = newsCommentService.createThread(savedNews.getId(), user.getId(), thread, List.of(testFile3, testFile4));

        // Then
        var threads = newsRepository.findAll().get(0).getThreads();
        assertEquals(threads.size(), 1);
        assertEquals(threads.get(0).getId(), savedThread.getId());
        assertEquals(threads.get(0).getFiles().size(), 2);
        assertEquals(threads.get(0).getUser().getId(), user.getId());
        assertEquals(threadFileRepository.findAll().size(), 4);
        assertEquals(threadRepository.findAll().size(), 1);
    }

    @ValueSource(strings = {"jpg"})
    @ParameterizedTest
    @Transactional
    @SneakyThrows
    public void createThreadCommentTest(String fileType) {
        // Given
        MultipartFile testFile1 = filesCreator.createTestFile(fileType, "test1");
        MultipartFile testFile2 = filesCreator.createTestFile(fileType, "test2");
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").role(Role.ROLE_USER).build();
        user = userRepository.save(user);
        var news = NewsEntity.builder().threads(new ArrayList<>()).name("post").text("text").user(user).build();
        var thread = ThreadEntity.builder().isAnonymous(false).text("Kapibara").childComments(new ArrayList<>()).build();
        var savedNews = newsService.createNewsWithFiles(news, user.getId(), List.of(testFile1, testFile2));
        var savedThread = newsCommentService.createThread(savedNews.getId(), user.getId(), thread, List.of(testFile1, testFile2));
        var threadComment = ThreadCommentEntity.builder().text("Kapibara").isAnonymous(true).build();
        // When
        System.out.println("Hello");
        var savedThreadComment = newsCommentService.createThreadComment(savedThread.getId(), user.getId(), threadComment, List.of(testFile1, testFile2));
        // Then
        var threadComments = threadRepository.findAll().get(0).getChildComments();
        assertEquals(threadComments.size(), 1);
        assertEquals(threadComments.get(0).getId(), savedThreadComment.getId());
        assertEquals(threadComments.get(0).getFiles().size(), 2);
        assertEquals(threadComments.get(0).getUser().getId(), user.getId());
        assertEquals(threadFileRepository.findAll().size(), 6);
        assertEquals(threadRepository.findAll().size(), 2);
    }


}

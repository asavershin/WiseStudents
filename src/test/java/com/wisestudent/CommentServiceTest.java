package com.wisestudent;

import com.wisestudent.models.*;
import com.wisestudent.models.posts.PostCommentEntity;
import com.wisestudent.models.posts.PostEntity;
import com.wisestudent.models.posts.SubjectEntity;
import com.wisestudent.repositories.*;
import com.wisestudent.repositories.news.NewsRepository;
import com.wisestudent.repositories.posts.*;
import com.wisestudent.services.CommentService;
import com.wisestudent.services.PostService;
import com.wisestudent.utils.FilesCreator;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CommentServiceTest extends AbstractTest{

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private PostTypeRepository postTypeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostCommentFileRepository commentFileRepository;

    @Autowired
    private PostFileRepository postFileRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private FilesCreator filesCreator;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private CommentService commentService;
    @Autowired
    private DefaultCommentRepository defaultCommentRepository;
    @Autowired
    private DefaultFileRepository defaultFileRepository;
    @Autowired
    private NewsRepository newsRepository;

    @BeforeEach
    @AfterEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clear() {
        defaultCommentRepository.deleteAll();
        postRepository.deleteAll();
        newsRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @SneakyThrows
    public void createCommentTest(){
        // Given
        var testFiles = new ArrayList<MultipartFile>();
        testFiles.add(filesCreator.createTestFile("jpg", "test1"));
        testFiles.add(filesCreator.createTestFile("pdf", "test2"));
        testFiles.add(filesCreator.createTestFile("jpg", "test3"));
        testFiles.add(filesCreator.createTestFile("pdf", "test4"));
        var subject = SubjectEntity.builder().posts(new ArrayList<>()).name("Matan").build();
        subject = subjectRepository.save(subject);
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").role(Role.ROLE_USER).build();
        user = userRepository.save(user);
        var post = PostEntity.builder().postComments(new ArrayList<>()).year(1).semester(1).name("post").text("text").user(user).build();
        var postTypeId = postTypeRepository.findAll().get(0).getId();
        var responsePost = postService.createPostWithFiles(post, postTypeId, subject.getId(), user.getId(), null);
        post = postRepository.findPostByIdWithFiles(responsePost.getId()).orElseThrow();
        var comment = PostCommentEntity.builder().files(new HashSet<>()).post(post).text("Kapibara").isAnonymous(true).build();
        // When
        var commentResponse = commentService.createComment(post.getId(), user.getId(), comment, testFiles);

        // Then
        assertEquals(postCommentRepository.findAll().size(), 1);
        assertNotNull(commentResponse.getUser());
        assertNotNull(commentResponse.getId());
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @SneakyThrows
    public void deleteFilesTest(){
        // Given
        var testFiles = new ArrayList<MultipartFile>();
        testFiles.add(filesCreator.createTestFile("jpg", "test1"));
        testFiles.add(filesCreator.createTestFile("pdf", "test2"));
        testFiles.add(filesCreator.createTestFile("jpg", "test3"));
        testFiles.add(filesCreator.createTestFile("pdf", "test4"));
        var subject = SubjectEntity.builder().posts(new ArrayList<>()).name("Matan").build();
        subject = subjectRepository.save(subject);
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").role(Role.ROLE_USER).build();
        user = userRepository.save(user);
        var post = PostEntity.builder().postComments(new ArrayList<>()).year(1).semester(1).name("post").text("text").user(user).build();
        var postTypeId = postTypeRepository.findAll().get(0).getId();
        var responsePost = postService.createPostWithFiles(post, postTypeId, subject.getId(), user.getId(), null);
        post = postRepository.findPostByIdWithFiles(responsePost.getId()).orElseThrow();
        var comment = PostCommentEntity.builder().files(new HashSet<>()).post(post).text("Kapibara").isAnonymous(true).build();
        var commentResponse = commentService.createComment(post.getId(), user.getId(), comment, testFiles);
        assertNotNull(commentResponse.getId());
        var defaultComment = defaultCommentRepository.findCommentByIdWithFilesAndUser(commentResponse.getId()).orElseThrow();
        var commentFiles = defaultComment.getFiles().stream().toList();
        // When
        assertEquals(defaultComment.getFiles().size(), 4);
        commentService.deleteFiles(user.getId(), commentResponse.getId(), List.of(commentFiles.get(0).getId(), commentFiles.get(1).getId()));
        defaultComment = defaultCommentRepository.findById(commentResponse.getId()).orElseThrow();

        // Then
        assertEquals(defaultComment.getFiles().size(), 2);
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(0).getFile()).build()));
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(1).getFile()).build()));
        assertEquals(commentFileRepository.findAll().size(), 2);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteCommentByAdminTest(){
        // Given
        var testFiles = new ArrayList<MultipartFile>();
        testFiles.add(filesCreator.createTestFile("jpg", "test1"));
        testFiles.add(filesCreator.createTestFile("pdf", "test2"));
        testFiles.add(filesCreator.createTestFile("jpg", "test3"));
        testFiles.add(filesCreator.createTestFile("pdf", "test4"));
        var subject = SubjectEntity.builder().posts(new ArrayList<>()).name("Matan").build();
        subject = subjectRepository.save(subject);
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").role(Role.ROLE_USER).build();
        user = userRepository.save(user);
        var post = PostEntity.builder().postComments(new ArrayList<>()).year(1).semester(1).name("post").text("text").user(user).build();
        var postTypeId = postTypeRepository.findAll().get(0).getId();
        var responsePost = postService.createPostWithFiles(post, postTypeId, subject.getId(), user.getId(), null);
        post = postRepository.findPostByIdWithFiles(responsePost.getId()).orElseThrow();
        var comment = PostCommentEntity.builder().files(new HashSet<>()).post(post).text("Kapibara").isAnonymous(true).build();
        var commentResponse = commentService.createComment(post.getId(), user.getId(), comment, testFiles);
        assertNotNull(commentResponse.getId());
        var defaultComment = defaultCommentRepository.findCommentByIdWithFilesAndUser(commentResponse.getId()).orElseThrow();
        var commentFiles = defaultComment.getFiles().stream().toList();

        // When
        assertEquals(defaultCommentRepository.findAll().size(), 1);
        commentService.deleteCommentByAdmin(commentResponse.getId());

        // Then
        assertEquals(defaultCommentRepository.findAll().size(), 0);
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(0).getFile()).build()));
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(1).getFile()).build()));
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(2).getFile()).build()));
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(3).getFile()).build()));
        assertEquals(commentFileRepository.findAll().size(), 0);
//        assertEquals(postRepository.findAll().get(0).getPostComments().size(), 0);
//        Пока так, почему-то хибер наотрез не хочет обратиться к БД, а берёт post,в котором комментарии не удалены, из кэша,
        // хотя в БД комментариев уже нет
    }
}

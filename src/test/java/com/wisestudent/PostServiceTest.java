package com.wisestudent;

import com.wisestudent.dto.post.PostFilter;
import com.wisestudent.models.*;
import com.wisestudent.models.posts.PostCommentEntity;
import com.wisestudent.models.posts.PostEntity;
import com.wisestudent.models.posts.SubjectEntity;
import com.wisestudent.repositories.*;
import com.wisestudent.repositories.posts.*;
import com.wisestudent.services.CommentService;
import com.wisestudent.services.FileService;
import com.wisestudent.services.PostService;
import com.wisestudent.utils.FilesCreator;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class PostServiceTest extends AbstractTest{
    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private PostTypeRepository postTypeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostFileRepository postFileRepository;

    @Autowired
    private PostCommentFileRepository commentFileRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private FilesCreator filesCreator;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private FileService fileService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private DefaultCommentRepository defaultCommentRepository;
    @Autowired
    private DefaultFileRepository defaultFileRepository;

    @BeforeEach
    @AfterEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clear() {
        defaultCommentRepository.deleteAll();
        postCommentRepository.deleteAll();
        postRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
    }


    @ValueSource(strings = {"jpg", "pdf"})
    @ParameterizedTest
    @Transactional
    @SneakyThrows
    public void postServiceTest(String fileType) {
        // Given
        MultipartFile testFile = filesCreator.createTestFile(fileType, "test");
        var subject = SubjectEntity.builder().posts(new ArrayList<>()).name("Matan").build();
        subject = subjectRepository.save(subject);
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").role(Role.ROLE_USER).build();
        user = userRepository.save(user);
        var post = PostEntity.builder().postComments(new ArrayList<>()).year(1).semester(1).name("post").text("text").user(user).build();
        var postTypeId = postTypeRepository.findAll().get(0).getId();

        // When
        var responsePost = postService.createPostWithFiles(post, postTypeId, subject.getId(), user.getId(), List.of(testFile));
        var posts = postService.filterPosts(0, 50, new PostFilter(null, null, null));
        var file = posts.get(0).getFiles().get(0);

        //Then
        assertNotNull(posts);
        assertNotNull(file);
        assertEquals(posts.size(), 1);
        assertEquals(Files.probeContentType(Path.of("." + fileType)), file.getType());
        assertEquals(responsePost.getPostType().getId(), postTypeId);
        assertEquals(responsePost.getText(), "text");
    }
    @Test
    @Transactional
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
        var responsePost = postService.createPostWithFiles(post, postTypeId, subject.getId(), user.getId(), testFiles);
        post = postRepository.findPostByIdWithFiles(responsePost.getId()).orElseThrow();
        var postFiles = post.getFiles().stream().toList();
        // When
        postService.deleteFiles(user.getId(), post.getId(), List.of(postFiles.get(0).getId(), postFiles.get(1).getId()));
        post = postRepository.findById(post.getId()).orElseThrow();

        // Then
        assertEquals(post.getFiles().size(), 2);
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(postFiles.get(0).getFile()).build()));
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(postFiles.get(0).getFile()).build()));
        assertEquals(postFileRepository.findAll().size(), 2);
    }

    @Test
    @Transactional
    public void deletePostByAdminTest(){
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
        var responsePost = postService.createPostWithFiles(post, postTypeId, subject.getId(), user.getId(), testFiles);
        post = postRepository.findPostByIdWithFiles(responsePost.getId()).orElseThrow();
        var comment = PostCommentEntity.builder().files(new HashSet<>()).post(post).text("Kapibara").isAnonymous(true).build();
        var commentResponse = commentService.createComment(post.getId(), user.getId(), comment, testFiles);
        assertNotNull(commentResponse.getId());
        var defaultComment = defaultCommentRepository.findCommentByIdWithFilesAndUser(commentResponse.getId()).orElseThrow();
        var commentFiles = defaultComment.getFiles().stream().toList();

        // When
        assertEquals(postFileRepository.findAll().size(), 8);
        postService.deletePostByAdmin(post.getId());
        // Then
        assertEquals(postRepository.findAll().size(), 0);
        assertEquals(postCommentRepository.findAll().size(), 0);
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(0).getFile()).build()));
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(1).getFile()).build()));
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(2).getFile()).build()));
        assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(commentFiles.get(3).getFile()).build()));
        assertEquals(postFileRepository.findAll().size(), 0);
    }
}


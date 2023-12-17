package com.wisestudent;

import com.wisestudent.exceptions.DuplicateException;
import com.wisestudent.models.posts.PostCommentEntity;
import com.wisestudent.models.posts.PostEntity;
import com.wisestudent.models.Role;
import com.wisestudent.models.posts.SubjectEntity;
import com.wisestudent.models.UserEntity;
import com.wisestudent.repositories.DefaultCommentRepository;
import com.wisestudent.repositories.DefaultFileRepository;
import com.wisestudent.repositories.posts.*;
import com.wisestudent.repositories.posts.PostCommentFileRepository;
import com.wisestudent.repositories.UserRepository;
import com.wisestudent.services.CommentService;
import com.wisestudent.services.PostService;
import com.wisestudent.services.SubjectService;
import com.wisestudent.utils.FilesCreator;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SubjectServiceTest extends AbstractTest {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private PostTypeRepository postTypeRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private CommentService commentService;

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
    private SubjectService subjectService;

    @Autowired
    private FilesCreator filesCreator;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private DefaultCommentRepository defaultCommentRepository;
    @Autowired
    private DefaultFileRepository defaultFileRepository;

    @BeforeEach
    @AfterEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clear() {
        defaultCommentRepository.deleteAll();
        postRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createSubjectTest(){
        // Given
        var subject = SubjectEntity.builder().name("Матан").posts(new ArrayList<>()).build();
        // When
        var newSubject = subjectService.createSubject(subject);
        var exception = assertThrows(DuplicateException.class, () -> subjectService.createSubject(subject));
        // Then
        assertEquals(subjectRepository.findAll().size(), 1);
        assertNotNull(newSubject);
        assertEquals(newSubject.getName(), subject.getName());
        assertEquals(exception.getFieldName(), "name");
        assertEquals(exception.getMessage(), "Предмет с таким именем " + subject.getName() + " уже есть");
    }

    @Test
    @Transactional
    public void updateSubjectTest(){
        // Given
        var subject = SubjectEntity.builder().name("Матан").posts(new ArrayList<>()).build();
        var postType = postTypeRepository.findAll().get(0);
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").role(Role.ROLE_USER).build();
        var post = PostEntity.builder().postComments(new ArrayList<>()).files(new HashSet<>()).postType(postType)
                .name("Слив").text("Мега").semester(1).year(1).subject(subject).user(user).build();
        postType.getPosts().add(post);
        subject.getPosts().add(post);
        subject = subjectRepository.save(subject);
        // When
        var newSubject = subjectService.updateSubject(
                SubjectEntity.builder().name("Теорвер").posts(new ArrayList<>()).id(subject.getId()).build()
        );
        var exception = assertThrows(DuplicateException.class, () -> subjectService.updateSubject(newSubject));
        // Then
        assertEquals(newSubject.getPosts().size(), 1);
        assertEquals(newSubject.getPosts().get(0).getName(),post.getName());
        assertEquals(newSubject.getName(), subject.getName());
        assertEquals(subject.getId(), newSubject.getId());
        assertEquals(exception.getMessage(), "Предмет с таким именем " + newSubject.getName() + " уже есть");
    }

    @Test
    public void deleteSubjectByAdminTest(){
        // Given
        var testCommentFiles = new ArrayList<MultipartFile>();
        testCommentFiles.add(filesCreator.createTestFile("jpg", "test1"));
        testCommentFiles.add(filesCreator.createTestFile("pdf", "test2"));
        testCommentFiles.add(filesCreator.createTestFile("jpg", "test3"));
        testCommentFiles.add(filesCreator.createTestFile("pdf", "test4"));

        var testPostFiles = new ArrayList<MultipartFile>();
        testPostFiles.add(filesCreator.createTestFile("jpg", "test5"));
        testPostFiles.add(filesCreator.createTestFile("pdf", "test6"));
        testPostFiles.add(filesCreator.createTestFile("jpg", "test7"));
        testPostFiles.add(filesCreator.createTestFile("pdf", "test8"));

        var subject = SubjectEntity.builder().posts(new ArrayList<>()).name("Matan").build();
        subject = subjectRepository.save(subject);
        var user = UserEntity.builder().name("Vasya").login("Login").password("123").role(Role.ROLE_USER).posts(new ArrayList<>()).comments(new ArrayList<>()).build();
        user = userRepository.save(user);
        var post = PostEntity.builder().postComments(new ArrayList<>()).year(1).semester(1).name("post").text("text").user(user).build();
        var postTypeId = postTypeRepository.findAll().get(0).getId();
        var responsePost = postService.createPostWithFiles(post, postTypeId, subject.getId(), user.getId(), testPostFiles);
        post = postRepository.findPostByIdWithFiles(responsePost.getId()).orElseThrow();
        var postFiles = post.getFiles().stream().toList();
        var comment = PostCommentEntity.builder().files(new HashSet<>()).post(post).text("Kapibara").isAnonymous(true).build();
        var commentResponse = commentService.createComment(post.getId(), user.getId(), comment, testCommentFiles);
        assertNotNull(commentResponse.getId());
        var defaultComment = defaultCommentRepository.findCommentByIdWithFilesAndUser(commentResponse.getId()).orElseThrow();
        var commentFiles = defaultComment.getFiles().stream().toList();

        // When
        subjectService.deleteSubjectByAdmin(subject.getId());

        // Then
        assertEquals(0, postCommentRepository.findAll().size());
        assertEquals(0, postRepository.findAll().size());
        assertEquals(0, subjectRepository.findAll().size());
        assertEquals(0, commentFileRepository.findAll().size());
        assertEquals(0, postFileRepository.findAll().size());
        assertEquals(1, userRepository.findAll().size());

        commentFiles.forEach(file ->
                assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(file.getFile()).build()))
        );

        postFiles.forEach(file ->
                assertThrows(Exception.class, () -> minioClient.getObject(GetObjectArgs.builder().bucket("files").object(file.getFile()).build()))
        );
    }
}

package com.wisestudent;

import com.wisestudent.config.MinioConfig;
import com.wisestudent.models.Role;
import com.wisestudent.models.UserEntity;
import com.wisestudent.properties.MinioProperties;
import com.wisestudent.repositories.DefaultCommentRepository;
import com.wisestudent.repositories.DefaultFileRepository;
import com.wisestudent.repositories.posts.PostCommentFileRepository;
import com.wisestudent.repositories.UserRepository;
import com.wisestudent.models.posts.PostCommentEntity;
import com.wisestudent.models.posts.PostEntity;
import com.wisestudent.models.posts.SubjectEntity;
import com.wisestudent.repositories.posts.*;
import com.wisestudent.services.CommentService;
import com.wisestudent.services.CommentsCleanupJobService;
import com.wisestudent.services.FileService;
import com.wisestudent.services.PostService;
import com.wisestudent.utils.FilesCreator;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class CommentsCleanupJobServiceTest extends AbstractTest{
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
    private CommentService commentService;

    @Autowired
    private FilesCreator filesCreator;

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private FileService fileService;
    @Autowired
    private CommentsCleanupJobService commentsCleanupJobService;
    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private DefaultCommentRepository defaultCommentRepository;

    @BeforeEach
    @AfterEach
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clear() {
        defaultCommentRepository.deleteAll();
        postRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
        try {
            Iterable<Result<Item>> files = minioClient.listObjects(ListObjectsArgs.builder().bucket(minioProperties.getBucket()).build());

            for (var file : files) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(minioProperties.getBucket())
                                .object(file.get().objectName())
                                .build());
            }
        } catch (Exception e) {
            log.info("Clean bucket fail");
            e.printStackTrace();
        }
    }

    @Test
    @SneakyThrows
    public void deleteOldCommentsTest(){
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

        var postTypeId = postTypeRepository.findAll().get(0).getId();

        var timeInPast = LocalDateTime.now().minusYears(5);

        var post = PostEntity.builder().postComments(new ArrayList<>()).year(1).semester(1).name("post").text("text").user(user).build();
        var responsePostNew = postService.createPostWithFiles(post, postTypeId, subject.getId(), user.getId(), testFiles);
        post = postRepository.findPostByIdWithFiles(responsePostNew.getId()).orElseThrow();


        var commentOld = PostCommentEntity.builder().text("Text").user(user).post(post).isAnonymous(false).build();
        commentService.createComment(post.getId(), user.getId(), commentOld, testFiles);
        commentOld = postCommentRepository.findById(commentOld.getId()).orElseThrow();
        postCommentRepository.save(commentOld.toBuilder().createdAt(timeInPast).build());

        var commentNew = PostCommentEntity.builder().text("Text").user(user).post(post).isAnonymous(false).build();
        commentService.createComment(post.getId(), user.getId(), commentNew, testFiles);

        // When
        commentsCleanupJobService.execute(null);

        // Then
        var postsSaved = postRepository.findAll();
        assertEquals(1, postsSaved.size());
        assertEquals(post.getId(), postsSaved.get(0).getId());

        var commentsSaved = postCommentRepository.findAll();
        assertEquals(1, commentsSaved.size());
        assertEquals(commentNew.getId(), commentsSaved.get(0).getId());

        var filesSaved = commentFileRepository.findAll();
        assertEquals(8, filesSaved.size());

        var objectsInMinio = minioClient.listObjects(ListObjectsArgs.builder().bucket("files").build());
        AtomicInteger countObjectsInMinio = new AtomicInteger();
        objectsInMinio.forEach( it -> countObjectsInMinio.addAndGet(1));
        assertEquals(8, countObjectsInMinio.get());
    }
}


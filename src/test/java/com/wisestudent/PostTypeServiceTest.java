package com.wisestudent;

import com.wisestudent.exceptions.DuplicateException;
import com.wisestudent.models.*;
import com.wisestudent.models.posts.PostEntity;
import com.wisestudent.models.posts.PostTypeEntity;
import com.wisestudent.models.posts.SubjectEntity;
import com.wisestudent.repositories.*;
import com.wisestudent.repositories.posts.*;
import com.wisestudent.services.PostTypeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class PostTypeServiceTest extends AbstractTest{
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
    private PostTypeService postTypeService;

    @BeforeEach
    @AfterEach
    public void clear() {
        postCommentRepository.deleteAll();
        postRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
        postFileRepository.deleteAll();
        commentFileRepository.deleteAll();
    }

    @Test
    public void createPostTypeTest(){
        // Given
        var postType = PostTypeEntity.builder().name("Коллоквиум").posts(new ArrayList<>()).build();
        var postTypeNum = postTypeRepository.findAll().size();
        // When
        var newPT = postTypeService.createPostType(postType);
        var exception = assertThrows(DuplicateException.class, () -> postTypeService.createPostType(postType));
        // Then
        assertEquals(postTypeRepository.findAll().size(), postTypeNum + 1);
        assertNotNull(newPT);
        assertEquals(newPT.getName(), postType.getName());
        assertEquals(exception.getFieldName(), "name");
        assertEquals(exception.getMessage(), "Тип поста с таким именем " + postType.getName() + " уже есть");
        // Clear
        postTypeRepository.delete(newPT);
    }

    @Test
    @Transactional
    public void updatePostTypeTest(){
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
        var newPT = postTypeService.updatePostType(
                PostTypeEntity.builder().name("Коллоквиум").posts(new ArrayList<>()).id(postType.getId()).build()
        );
        var exception = assertThrows(DuplicateException.class, () -> postTypeService.updatePostType(newPT));
        // Then
        assertEquals(newPT.getPosts().size(), 1);
        assertEquals(newPT.getPosts().get(0).getName(),post.getName());
        assertEquals(newPT.getName(), postType.getName());
        assertEquals(subject.getId(), newPT.getId());
        assertEquals(exception.getMessage(), "Тип поста с таким именем " + newPT.getName() + " уже есть");
    }
}

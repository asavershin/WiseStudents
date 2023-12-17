package com.wisestudent.repositories.posts;

import com.querydsl.jpa.impl.JPAQuery;
import com.wisestudent.dto.post.PostFilter;
import com.wisestudent.models.*;
import com.wisestudent.models.posts.PostEntity;
import com.wisestudent.models.posts.QPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostQuerdslRepository {
    private final EntityManager entityManager;

    public List<PostEntity> postFilterByYearSubjectPostType(int pageNumber, int pageSize, PostFilter filter) {
        var post = QPostEntity.postEntity;

        JPAQuery<PostEntity> query = new JPAQuery<>(entityManager);

        return query.select(post).from(post)
                .leftJoin(post.postType).fetchJoin()
                .leftJoin(post.subject).fetchJoin()
                .leftJoin(post.files).fetchJoin()
                .leftJoin(post.user).fetchJoin()
                .where(filter.toPredicate())
                .offset(pageNumber * pageSize)
                .limit(pageSize)
                .fetch();
    }
}

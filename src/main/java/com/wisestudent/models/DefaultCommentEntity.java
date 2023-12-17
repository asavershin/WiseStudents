package com.wisestudent.models;

import com.wisestudent.models.news.NewsEntity;
import com.wisestudent.models.news.ThreadCommentEntity;
import com.wisestudent.models.news.ThreadEntity;
import com.wisestudent.models.posts.PostEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Audited
@Table(name = "comments_jn")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DefaultCommentEntity implements Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_jn_id_seq")
    @SequenceGenerator(name = "comments_jn_id_seq", sequenceName = "comments_jn_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous;

    @Column(name = "text", nullable = false)
    private String text;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotAudited
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "comment",cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<DefaultFileEntity> files = new HashSet<>();

    @NotAudited
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DefaultCommentEntity comment;

    @NotAudited
    @OneToMany(mappedBy = "comment", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<DefaultCommentEntity> childComments = new ArrayList<>();

    @NotAudited
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "news_comments",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private NewsEntity news;

    @NotAudited
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "posts_comments",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PostEntity post;

}

package com.wisestudent.models.news;

import com.wisestudent.models.Comment;
import com.wisestudent.models.UserEntity;
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
public class ThreadEntity implements Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_jn_id_seq")
    @SequenceGenerator(name = "comments_jn_id_seq", sequenceName = "comments_jn_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous;

    @Column(name = "text", nullable = false)
    private String text;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @NotAudited
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "thread",cascade = {CascadeType.ALL})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ThreadFileEntity> files = new HashSet<>();

    @NotAudited
    @OneToMany(mappedBy = "parentComment", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ThreadCommentEntity> childComments = new ArrayList<>();
}

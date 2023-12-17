package com.wisestudent.models.news;

import com.wisestudent.models.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "news_jn")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_jn_id_seq")
    @SequenceGenerator(name = "news_jn_id_seq", sequenceName = "news_jn_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "text", nullable = false)
    private String text;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "news", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ThreadEntity> threads = new ArrayList<>();


    @OneToMany(mappedBy = "news", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<NewsFileEntity> files = new HashSet<>();
}

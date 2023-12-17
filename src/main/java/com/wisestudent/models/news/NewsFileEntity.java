package com.wisestudent.models.news;

import com.wisestudent.models.File;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "files_jn")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsFileEntity implements File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_jn_id_seq")
    @SequenceGenerator(name = "files_jn_id_seq", sequenceName = "files_jn_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "file", nullable = false)
    private String file;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "files_news",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private NewsEntity news;
}


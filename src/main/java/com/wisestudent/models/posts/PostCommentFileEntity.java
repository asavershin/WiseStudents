package com.wisestudent.models.posts;

import com.wisestudent.models.File;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Audited
@Table(name = "files_jn")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentFileEntity implements File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_jn_id_seq")
    @SequenceGenerator(name = "files_jn_id_seq", sequenceName = "files_jn_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "file", nullable = false)
    private String file;

    @ManyToMany(mappedBy = "files", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<PostCommentEntity> comments = new HashSet<>();
}

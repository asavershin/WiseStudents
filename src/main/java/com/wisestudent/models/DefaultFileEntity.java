package com.wisestudent.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Data
@Audited
@Table(name = "files_jn")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefaultFileEntity implements File {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_jn_id_seq")
    @SequenceGenerator(name = "files_jn_id_seq", sequenceName = "files_jn_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "file", nullable = false)
    private String file;

    @NotAudited
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "files_comments",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private DefaultCommentEntity comment;
}

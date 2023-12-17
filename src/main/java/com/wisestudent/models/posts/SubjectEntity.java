package com.wisestudent.models.posts;

import com.wisestudent.models.posts.PostEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "subjects_ref")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subjects_ref_id_seq")
    @SequenceGenerator(name = "subjects_ref_id_seq", sequenceName = "subjects_ref_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "subject", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PostEntity> posts = new ArrayList<>();
}

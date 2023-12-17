package com.wisestudent.models.posts;

import com.wisestudent.models.posts.PostEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "post_type_ref")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_type_ref_id_seq")
    @SequenceGenerator(name = "post_type_ref_id_seq", sequenceName = "post_type_ref_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "postType", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PostEntity> posts = new ArrayList<>();
}

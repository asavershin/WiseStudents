package com.wisestudent.dto.post;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.wisestudent.dto.PageDto;
import com.wisestudent.models.posts.QPostEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostFilter extends PageDto {
    @Min(1)
    @Max(6)
    private Integer year;
    @Min(1)
    private Long subjectId;
    @Min(1)
    private Long postTypeId;
    public Predicate toPredicate() {
        var postFilter = QPostEntity.postEntity;
        List<Predicate> predicates = new ArrayList<>();

        if (nonNull(year)) {
            predicates.add(postFilter.year.eq(year));
        }

        if (nonNull(subjectId)) {
            predicates.add(postFilter.subject.id.eq(subjectId));
        }

        if (nonNull(postTypeId)) {
            predicates.add(postFilter.postType.id.eq(postTypeId));
        }

        if (predicates.isEmpty()) {
            return Expressions.TRUE;
        } else {
            return ExpressionUtils.allOf(predicates);
        }
    }
}

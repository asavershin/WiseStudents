package com.wisestudent.mappers;

import com.wisestudent.dto.Subject.RequestPutSubject;
import com.wisestudent.dto.Subject.RequestSubject;
import com.wisestudent.dto.Subject.ResponseSubject;
import com.wisestudent.models.posts.SubjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    @Mapping(target = "name", source = "name")
    SubjectEntity requestSubjectToSubject(RequestSubject requestSubject);
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SubjectEntity requestSubjectToSubject(RequestPutSubject request);
    ResponseSubject subjectToResponseSubject(SubjectEntity subject);
    List<ResponseSubject> listSubjectsToListResponseSubjects(List<SubjectEntity> subjects);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "posts", ignore = true)
    void updateSubject(@MappingTarget SubjectEntity oldSubject, SubjectEntity newSubject);
}

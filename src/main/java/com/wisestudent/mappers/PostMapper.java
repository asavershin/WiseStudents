package com.wisestudent.mappers;

import com.wisestudent.dto.FileDto;
import com.wisestudent.dto.post.RequestPost;
import com.wisestudent.dto.post.ResponsePost;
import com.wisestudent.dto.post.ResponsePostWithoutFiles;
import com.wisestudent.models.posts.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SubjectMapper.class, PostTypeMapper.class, UserMapper.class})
public interface PostMapper {
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "postType", ignore = true)
    @Mapping(target = "user", ignore = true)
    PostEntity requestPostToPost(RequestPost requestPost);

    @Mapping(target = "files", source = "files")
    ResponsePost postWithFilesToResponsePost(PostEntity post, List<FileDto> files);

    ResponsePostWithoutFiles postWithFilesToResponsePost(PostEntity post);
}

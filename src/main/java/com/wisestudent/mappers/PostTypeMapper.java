package com.wisestudent.mappers;

import com.wisestudent.dto.posttype.RequestPostType;
import com.wisestudent.dto.posttype.RequestPutPostType;
import com.wisestudent.dto.posttype.ResponsePostType;
import com.wisestudent.models.posts.PostTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostTypeMapper {

    ResponsePostType postTypeToResponsePostType(PostTypeEntity postType);
    List<ResponsePostType> listPTtoListResponsePT(List<PostTypeEntity> postTypes);
    @Mapping(target = "name", source = "name")
    PostTypeEntity requestPostTypeToPostType(RequestPostType requestPostType);
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PostTypeEntity requestPostTypeToPostType(RequestPutPostType request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "posts", ignore = true)
    void updatePostType(@MappingTarget PostTypeEntity oldPT, PostTypeEntity newPT);
}

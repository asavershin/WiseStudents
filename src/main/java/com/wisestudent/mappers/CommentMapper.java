package com.wisestudent.mappers;

import com.wisestudent.dto.FileDto;
import com.wisestudent.dto.PageResponse;
import com.wisestudent.dto.comment.*;
import com.wisestudent.models.Comment;
import com.wisestudent.models.news.ThreadCommentEntity;
import com.wisestudent.models.news.ThreadEntity;
import com.wisestudent.models.posts.PostCommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.IntStream;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {
    @Mapping(target = "isAnonymous", source = "isAnonymous", qualifiedByName = "anon")
    PostCommentEntity requestToComment(RequestPostComment request);

    @Mapping(target = "content", expression = "java(mapToResponseComments(comments.getContent(), files))")
    @Mapping(target = "totalPages", source = "comments.totalPages")
    @Mapping(target = "totalSize", source = "comments.totalElements")
    @Mapping(target = "pageNumber", source = "comments.number")
    @Mapping(target = "pageSize", source = "comments.size")
    PageResponse<ResponseComment> pageCommentsToPageResponse(Page<PostCommentEntity> comments, List<List<FileDto>> files);

    default List<ResponseComment> mapToResponseComments(List<PostCommentEntity> comments, List<List<FileDto>> files) {
        return IntStream.range(0, comments.size())
                .mapToObj(i -> mapToResponseComment(comments.get(i), files.get(i)))
                .toList();
    }

    @Mapping(target = "files", source = "files")
    @Mapping(target = "user", source = "comment", qualifiedByName = "convertToAnon")
    ResponseComment mapToResponseComment(Comment comment, List<FileDto> files);

    @Mapping(target = "user", source = "comment", qualifiedByName = "convertToAnon")
    ResponseCommentWithoutFiles commentWithoutFilesToResponseComment(Comment comment);

    @Named("anon")
    default Boolean anon(String isAnonymous){
        return isAnonymous.equalsIgnoreCase("yes");
    }

    @Mapping(target = "isAnonymous", source = "isAnonymous", qualifiedByName = "anon")
    ThreadEntity requestToThread(RequestThread request);
    @Mapping(target = "isAnonymous", source = "isAnonymous", qualifiedByName = "anon")
    ThreadCommentEntity requestToThreadComment(RequestThreadComment request);
}

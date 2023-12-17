package com.wisestudent.mappers;

import com.wisestudent.dto.PageResponse;
import com.wisestudent.dto.user.RequestUser;
import com.wisestudent.dto.user.ResponseUser;
import com.wisestudent.models.Comment;
import com.wisestudent.models.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import com.wisestudent.models.Sex;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ResponseUser userToResponseUser(UserEntity user);

    @Mapping(target = "sex", expression = "java(getSex(user.getSex()))")
    UserEntity requestUserToUser(RequestUser user);

    @Mapping(target = "content", source = "users.content")
    @Mapping(target = "totalPages", source = "users.totalPages")
    @Mapping(target = "totalSize", source = "users.totalElements")
    @Mapping(target = "pageNumber", source = "users.number")
    @Mapping(target = "pageSize", source = "users.size")
    PageResponse<ResponseUser> pageUsersToPageResponse(Page<UserEntity> users);

    default Sex getSex(String sex) {
        return Sex.valueOf(sex);
    }

    @Named("convertToAnon")
    default ResponseUser convertToAnon(Comment comment){
        return this.userToResponseUser(comment.getIsAnonymous()?null:comment.getUser());
    }

}

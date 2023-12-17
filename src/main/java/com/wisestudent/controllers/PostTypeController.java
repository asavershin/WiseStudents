package com.wisestudent.controllers;

import com.wisestudent.dto.posttype.RequestPostType;
import com.wisestudent.dto.posttype.RequestPutPostType;
import com.wisestudent.dto.posttype.ResponsePostType;
import com.wisestudent.mappers.PostTypeMapper;
import com.wisestudent.services.PostTypeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@CrossOrigin
@RestController
@RequestMapping("/wise-students/post-types")
@RequiredArgsConstructor
public class PostTypeController {
    private final PostTypeService postTypesService;
    private final PostTypeMapper postTypeMapper;

    @GetMapping
    @Operation(description = "Получить все типы постов")
    public List<ResponsePostType> getPostTypes() {
        return postTypeMapper.listPTtoListResponsePT(postTypesService.getPostTypes());
    }

    @PostMapping
    @Operation(description = "Создать тип постов")
    public ResponsePostType createPostType(@Valid @RequestBody RequestPostType request) {
        return postTypeMapper.postTypeToResponsePostType(
                postTypesService.createPostType(
                        postTypeMapper.requestPostTypeToPostType(request)
                )
        );
    }

    @PutMapping
    @Operation(description = "Обновить тип поста по id")
    public ResponsePostType updatePostType(@Valid @RequestBody RequestPutPostType request) {
        return postTypeMapper.postTypeToResponsePostType(
                postTypesService.updatePostType(
                        postTypeMapper.requestPostTypeToPostType(request)
                )
        );
    }
}

package com.wisestudent.mappers;

import com.wisestudent.dto.FileDto;
import com.wisestudent.dto.news.RequestNews;
import com.wisestudent.dto.news.ResponseNews;
import com.wisestudent.dto.news.ResponseNewsWithoutFiles;
import com.wisestudent.models.news.NewsEntity;
import com.wisestudent.models.posts.PostEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface NewsMapper {
    @Mapping(target = "files", source = "files")
    ResponseNews newsWithFilesToResponsePost(NewsEntity news, List<FileDto> files);
    ResponseNewsWithoutFiles newsToResponseNewsWithoutFiles(NewsEntity news);

    NewsEntity requestNewsToNews(RequestNews request);
}

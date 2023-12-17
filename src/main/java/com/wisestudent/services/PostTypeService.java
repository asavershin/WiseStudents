package com.wisestudent.services;

import com.wisestudent.exceptions.DuplicateException;
import com.wisestudent.mappers.PostTypeMapper;
import com.wisestudent.models.posts.PostTypeEntity;
import com.wisestudent.repositories.posts.PostTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostTypeService {
    private final PostTypeRepository postTypeRepository;
    private final PostTypeMapper postTypeMapper;

    public List<PostTypeEntity> getPostTypes(){
        return postTypeRepository.findAll();
    }

    public PostTypeEntity createPostType(PostTypeEntity postType) {
        if(postTypeRepository.existsByName(postType.getName())){
            throw new DuplicateException("Тип поста с таким именем " + postType.getName() + " уже есть", "name");
        }
        return postTypeRepository.save(postType);
    }

    public PostTypeEntity updatePostType(PostTypeEntity newPT){
        var postType = postTypeRepository.findById(newPT.getId())
                .orElseThrow(() -> new EntityNotFoundException("Тип поста с id " + newPT.getId() + " не найден"));
        if(Objects.equals(postType.getName(), newPT.getName())){
            throw new DuplicateException("Тип поста с таким именем " + newPT.getName() + " уже есть", "name");
        }

        postTypeMapper.updatePostType(postType, newPT);
        return postTypeRepository.save(postType);
    }
}

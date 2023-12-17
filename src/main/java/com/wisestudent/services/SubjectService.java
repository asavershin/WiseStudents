package com.wisestudent.services;

import com.wisestudent.exceptions.DuplicateException;
import com.wisestudent.mappers.SubjectMapper;
import com.wisestudent.models.posts.SubjectEntity;
import com.wisestudent.repositories.posts.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final PostService postService;
    private final FileService fileService;

    public SubjectEntity createSubject(SubjectEntity subject){
        if(subjectRepository.existsByName(subject.getName())){
            throw new DuplicateException("Предмет с таким именем " + subject.getName() + " уже есть", "name");
        }
        return subjectRepository.save(subject);
    }

    public List<SubjectEntity> getSubjects() {
        return subjectRepository.findAll();
    }

    public SubjectEntity updateSubject(SubjectEntity newSubject){
        var subject = subjectRepository.findById(newSubject.getId())
                .orElseThrow(() -> new EntityNotFoundException("Предмет с id " + newSubject.getId() + " не найден"));
        if(Objects.equals(subject.getName(), newSubject.getName())){
            throw new DuplicateException("Предмет с таким именем " + newSubject.getName() + " уже есть", "name");
        }

        subjectMapper.updateSubject(subject, newSubject);
        return subjectRepository.save(subject);
    }

    @Transactional
    public void deleteSubjectByAdmin(Long id){
        var postFiles = subjectRepository.findAllFilesInPosts(id);
        var commentFiles = subjectRepository.findAllFilesInComments(id);
        subjectRepository.deleteById(id);
        fileService.deleteFiles(postFiles);
        fileService.deleteFiles(commentFiles);
    }
}

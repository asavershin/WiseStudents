package com.wisestudent.controllers;

import com.wisestudent.dto.Subject.RequestPutSubject;
import com.wisestudent.dto.Subject.RequestSubject;
import com.wisestudent.dto.Subject.ResponseSubject;
import com.wisestudent.mappers.SubjectMapper;
import com.wisestudent.models.Role;
import com.wisestudent.services.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
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
@RequestMapping("/wise-students/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;
    private final SubjectMapper subjectMapper;

    @PostMapping
    @Operation(description = "Создание предмета")
    public ResponseSubject createSubject(@Valid @RequestBody RequestSubject request) {
        return subjectMapper.subjectToResponseSubject(
                subjectService.createSubject(
                        subjectMapper.requestSubjectToSubject(request)
                )
        );
    }

    @GetMapping
    @Operation(description = "Получение предметов")
    public List<ResponseSubject> getObjects() {
        return subjectMapper.listSubjectsToListResponseSubjects(subjectService.getSubjects());
    }

    @PutMapping
    @Operation(description = "Обновить предмет по id")
    public ResponseSubject updateSubject(@RequestBody @Valid RequestPutSubject request) {
        return subjectMapper.subjectToResponseSubject(
                subjectService.updateSubject(
                        subjectMapper.requestSubjectToSubject(request)
                )
        );
    }
}

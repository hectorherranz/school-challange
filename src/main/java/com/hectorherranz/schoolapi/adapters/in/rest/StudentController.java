package com.hectorherranz.schoolapi.adapters.in.rest;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentResponse;
import com.hectorherranz.schoolapi.adapters.in.rest.mapper.StudentDtoMapper;
import com.hectorherranz.schoolapi.application.command.DeleteStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.*;
import com.hectorherranz.schoolapi.application.query.GetStudentByIdQuery;
import com.hectorherranz.schoolapi.application.query.SearchStudentsQuery;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final CreateStudentUseCase createStudentUseCase;
    private final GetStudentByIdUseCase getStudentByIdUseCase;
    private final UpdateStudentUseCase updateStudentUseCase;
    private final DeleteStudentUseCase deleteStudentUseCase;
    private final SearchStudentsUseCase searchStudentsUseCase;
    private final StudentDtoMapper studentDtoMapper;

    public StudentController(
            CreateStudentUseCase createStudentUseCase,
            GetStudentByIdUseCase getStudentByIdUseCase,
            UpdateStudentUseCase updateStudentUseCase,
            DeleteStudentUseCase deleteStudentUseCase,
            SearchStudentsUseCase searchStudentsUseCase,
            StudentDtoMapper studentDtoMapper) {
        this.createStudentUseCase = createStudentUseCase;
        this.getStudentByIdUseCase = getStudentByIdUseCase;
        this.updateStudentUseCase = updateStudentUseCase;
        this.deleteStudentUseCase = deleteStudentUseCase;
        this.searchStudentsUseCase = searchStudentsUseCase;
        this.studentDtoMapper = studentDtoMapper;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        var command = studentDtoMapper.toCreateCommand(request);
        UUID studentId = createStudentUseCase.handle(command);
        
        var query = new GetStudentByIdQuery(studentId);
        var student = getStudentByIdUseCase.handle(query);
        var response = studentDtoMapper.toResponse(student);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable UUID id) {
        var query = new GetStudentByIdQuery(id);
        var student = getStudentByIdUseCase.handle(query);
        var response = studentDtoMapper.toResponse(student);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable UUID id,
            @RequestParam String name) {
        var command = studentDtoMapper.toUpdateCommand(id, name);
        updateStudentUseCase.handle(command);
        
        var query = new GetStudentByIdQuery(id);
        var student = getStudentByIdUseCase.handle(query);
        var response = studentDtoMapper.toResponse(student);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        var command = new DeleteStudentCommand(id);
        deleteStudentUseCase.handle(command);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponse<StudentResponse>> searchStudents(
            @RequestParam UUID schoolId,
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        var searchQuery = new SearchStudentsQuery(schoolId, query, pageable);
        var result = searchStudentsUseCase.handle(searchQuery);
        
        var responses = result.content().stream()
                .map(studentDtoMapper::toResponse)
                .toList();
        
        var response = new PagedResponse<>(
                responses,
                result.pageNumber(),
                result.pageSize(),
                result.totalElements(),
                result.totalPages(),
                result.hasNext(),
                result.hasPrevious()
        );
        
        return ResponseEntity.ok(response);
    }
}

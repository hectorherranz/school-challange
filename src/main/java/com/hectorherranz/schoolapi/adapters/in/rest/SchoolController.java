package com.hectorherranz.schoolapi.adapters.in.rest;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolDetail;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolSummary;
import com.hectorherranz.schoolapi.adapters.in.rest.mapper.SchoolDtoMapper;
import com.hectorherranz.schoolapi.application.command.DeleteSchoolCommand;
import com.hectorherranz.schoolapi.application.port.in.*;
import com.hectorherranz.schoolapi.application.query.GetSchoolByIdQuery;
import com.hectorherranz.schoolapi.application.query.SearchSchoolsQuery;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {

    private final CreateSchoolUseCase createSchoolUseCase;
    private final GetSchoolByIdUseCase getSchoolByIdUseCase;
    private final UpdateSchoolUseCase updateSchoolUseCase;
    private final DeleteSchoolUseCase deleteSchoolUseCase;
    private final SearchSchoolsUseCase searchSchoolsUseCase;
    private final SchoolDtoMapper schoolDtoMapper;

    public SchoolController(
            CreateSchoolUseCase createSchoolUseCase,
            GetSchoolByIdUseCase getSchoolByIdUseCase,
            UpdateSchoolUseCase updateSchoolUseCase,
            DeleteSchoolUseCase deleteSchoolUseCase,
            SearchSchoolsUseCase searchSchoolsUseCase,
            SchoolDtoMapper schoolDtoMapper) {
        this.createSchoolUseCase = createSchoolUseCase;
        this.getSchoolByIdUseCase = getSchoolByIdUseCase;
        this.updateSchoolUseCase = updateSchoolUseCase;
        this.deleteSchoolUseCase = deleteSchoolUseCase;
        this.searchSchoolsUseCase = searchSchoolsUseCase;
        this.schoolDtoMapper = schoolDtoMapper;
    }

    @PostMapping
    public ResponseEntity<SchoolDetail> createSchool(@Valid @RequestBody SchoolRequest request) {
        var command = schoolDtoMapper.toCreateCommand(request);
        UUID schoolId = createSchoolUseCase.handle(command);
        
        var query = new GetSchoolByIdQuery(schoolId);
        var school = getSchoolByIdUseCase.handle(query);
        var response = schoolDtoMapper.toDetail(school);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolDetail> getSchoolById(@PathVariable UUID id) {
        var query = new GetSchoolByIdQuery(id);
        var school = getSchoolByIdUseCase.handle(query);
        var response = schoolDtoMapper.toDetail(school);
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolDetail> updateSchool(
            @PathVariable UUID id,
            @Valid @RequestBody SchoolRequest request) {
        var command = schoolDtoMapper.toUpdateCommand(id, request);
        updateSchoolUseCase.handle(command);
        
        var query = new GetSchoolByIdQuery(id);
        var school = getSchoolByIdUseCase.handle(query);
        var response = schoolDtoMapper.toDetail(school);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchool(@PathVariable UUID id) {
        var command = new DeleteSchoolCommand(id);
        deleteSchoolUseCase.handle(command);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponse<SchoolSummary>> searchSchools(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        var searchQuery = new SearchSchoolsQuery(query, pageable);
        var result = searchSchoolsUseCase.handle(searchQuery);
        
        var summaries = result.content().stream()
                .map(schoolDtoMapper::toSummary)
                .toList();
        
        var response = new PagedResponse<>(
                summaries,
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

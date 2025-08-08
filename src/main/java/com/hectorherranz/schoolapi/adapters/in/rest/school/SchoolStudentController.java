package com.hectorherranz.schoolapi.adapters.in.rest.school;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentResponse;
import com.hectorherranz.schoolapi.adapters.in.rest.mapper.StudentDtoMapper;
import com.hectorherranz.schoolapi.application.port.in.*;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schools/{schoolId}/students")
@Tag(name = "School Students", description = "Student management operations within a school")
public class SchoolStudentController {

  private final CreateStudentUseCase createStudentUseCase;
  private final GetStudentByIdUseCase getStudentByIdUseCase;
  private final UpdateStudentUseCase updateStudentUseCase; // Hybrid approach (optimized by default)
  private final DeleteStudentUseCase deleteStudentUseCase;
  private final SearchStudentsUseCase searchStudentsUseCase;
  private final StudentDtoMapper studentDtoMapper;

  public SchoolStudentController(
      CreateStudentUseCase createStudentUseCase,
      GetStudentByIdUseCase getStudentByIdUseCase,
      UpdateStudentUseCase updateStudentUseCase, // Hybrid approach (optimized by default)
      DeleteStudentUseCase deleteStudentUseCase,
      SearchStudentsUseCase searchStudentsUseCase,
      StudentDtoMapper studentDtoMapper) {
    this.createStudentUseCase = createStudentUseCase;
    this.getStudentByIdUseCase = getStudentByIdUseCase;
    this.updateStudentUseCase = updateStudentUseCase; // Uses hybrid approach
    this.deleteStudentUseCase = deleteStudentUseCase;
    this.searchStudentsUseCase = searchStudentsUseCase;
    this.studentDtoMapper = studentDtoMapper;
  }

  @PostMapping
  @Operation(
      summary = "Enroll a new student in the school",
      description = "Enrolls a new student in the specified school")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Student enrolled successfully",
            content = @Content(schema = @Schema(implementation = StudentResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "School not found"),
        @ApiResponse(responseCode = "409", description = "School capacity exceeded")
      })
  public ResponseEntity<StudentResponse> enrollStudent(
      @PathVariable UUID schoolId, @Valid @RequestBody StudentRequest request) {
    var command = studentDtoMapper.toCreateCommand(request, schoolId);
    UUID studentId = createStudentUseCase.handle(command);

    var query = studentDtoMapper.toGetStudentQuery(studentId, schoolId);
    var student = getStudentByIdUseCase.handle(query);
    var response = studentDtoMapper.toResponse(student);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{studentId}")
  @Operation(
      summary = "Get student details",
      description = "Retrieves details of a specific student in the school")
  public ResponseEntity<StudentResponse> getStudentById(
      @PathVariable UUID schoolId, @PathVariable UUID studentId) {
    var query = studentDtoMapper.toGetStudentQuery(studentId, schoolId);
    var student = getStudentByIdUseCase.handle(query);
    var response = studentDtoMapper.toResponse(student);

    return ResponseEntity.ok(response);
  }

  @PutMapping("/{studentId}")
  @Operation(
      summary = "Update student",
      description = "Updates a student's information using hybrid approach (optimized by default)")
  public ResponseEntity<StudentResponse> updateStudent(
      @PathVariable UUID schoolId,
      @PathVariable UUID studentId,
      @Valid @RequestBody StudentRequest request) {
    var command = studentDtoMapper.toUpdateCommand(request, schoolId, studentId);
    updateStudentUseCase.handle(command); // Uses hybrid approach

    var query = studentDtoMapper.toGetStudentQuery(studentId, schoolId);
    var student = getStudentByIdUseCase.handle(query);
    var response = studentDtoMapper.toResponse(student);

    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{studentId}")
  @Operation(summary = "Remove student", description = "Removes a student from the school")
  public ResponseEntity<Void> removeStudent(
      @PathVariable UUID schoolId, @PathVariable UUID studentId) {
    var command = studentDtoMapper.toDeleteCommand(schoolId, studentId);
    deleteStudentUseCase.handle(command);

    return ResponseEntity.noContent().build();
  }

  @GetMapping
  @Operation(
      summary = "List and search students",
      description = "Lists and searches students in the school")
  public ResponseEntity<PagedResponse<StudentResponse>> listStudents(
      @PathVariable UUID schoolId,
      @RequestParam(required = false, defaultValue = "") String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    Pageable pageable = PageRequest.of(page, size);
    var searchQuery = studentDtoMapper.toSearchQuery(schoolId, query, pageable);
    var result = searchStudentsUseCase.handle(searchQuery);

    var responses = result.content().stream().map(studentDtoMapper::toResponse).toList();

    var pagedResponse =
        new PagedResponse<>(
            responses,
            result.pageNumber(),
            result.pageSize(),
            result.totalElements(),
            result.totalPages(),
            result.hasNext(),
            result.hasPrevious());

    return ResponseEntity.ok(pagedResponse);
  }
}

package com.hectorherranz.schoolapi.adapters.in.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentResponse;
import com.hectorherranz.schoolapi.adapters.in.rest.mapper.StudentDtoMapper;
import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;
import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.*;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import com.hectorherranz.schoolapi.domain.exception.CapacityExceededException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.Student;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

  @Mock private CreateStudentUseCase createStudentUseCase;

  @Mock private GetStudentByIdUseCase getStudentByIdUseCase;

  @Mock private UpdateStudentUseCase updateStudentUseCase;

  @Mock private DeleteStudentUseCase deleteStudentUseCase;

  @Mock private SearchStudentsUseCase searchStudentsUseCase;

  @Mock private StudentDtoMapper studentDtoMapper;

  private StudentController controller;

  @BeforeEach
  void setUp() {
    controller =
        new StudentController(
            createStudentUseCase,
            getStudentByIdUseCase,
            updateStudentUseCase,
            deleteStudentUseCase,
            searchStudentsUseCase,
            studentDtoMapper);
  }

  @Test
  void givenValidRequest_whenCreateStudent_thenReturns201WithLocation() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("Harry Potter", schoolId);
    CreateStudentCommand expectedCommand = new CreateStudentCommand("Harry Potter", schoolId);
    Student student = new Student(studentId, "Harry Potter", schoolId);
    StudentResponse expectedResponse = new StudentResponse(studentId, "Harry Potter", schoolId);

    when(studentDtoMapper.toCreateCommand(request)).thenReturn(expectedCommand);
    when(createStudentUseCase.handle(expectedCommand)).thenReturn(studentId);
    when(getStudentByIdUseCase.handle(any())).thenReturn(student);
    when(studentDtoMapper.toResponse(student)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<StudentResponse> response = controller.createStudent(request);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().id());
    assertEquals("Harry Potter", response.getBody().name());
    assertEquals(schoolId, response.getBody().schoolId());

    verify(studentDtoMapper).toCreateCommand(request);
    verify(createStudentUseCase).handle(expectedCommand);
    verify(getStudentByIdUseCase).handle(any());
    verify(studentDtoMapper).toResponse(student);
  }

  @Test
  void givenValidId_whenGetStudentById_thenReturns200() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    Student student = new Student(studentId, "Harry Potter", schoolId);
    StudentResponse expectedResponse = new StudentResponse(studentId, "Harry Potter", schoolId);

    when(getStudentByIdUseCase.handle(any())).thenReturn(student);
    when(studentDtoMapper.toResponse(student)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<StudentResponse> response = controller.getStudentById(studentId);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().id());
    assertEquals("Harry Potter", response.getBody().name());
    assertEquals(schoolId, response.getBody().schoolId());

    verify(getStudentByIdUseCase).handle(any());
    verify(studentDtoMapper).toResponse(student);
  }

  @Test
  void givenValidRequest_whenUpdateStudent_thenReturns200() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    String newName = "Updated Student Name";
    UpdateStudentCommand expectedCommand = new UpdateStudentCommand(studentId, newName);
    Student student = new Student(studentId, newName, schoolId);
    StudentResponse expectedResponse = new StudentResponse(studentId, newName, schoolId);

    when(studentDtoMapper.toUpdateCommand(studentId, newName)).thenReturn(expectedCommand);
    doNothing().when(updateStudentUseCase).handle(expectedCommand);
    when(getStudentByIdUseCase.handle(any())).thenReturn(student);
    when(studentDtoMapper.toResponse(student)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<StudentResponse> response = controller.updateStudent(studentId, newName);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().id());
    assertEquals(newName, response.getBody().name());
    assertEquals(schoolId, response.getBody().schoolId());

    verify(studentDtoMapper).toUpdateCommand(studentId, newName);
    verify(updateStudentUseCase).handle(expectedCommand);
    verify(getStudentByIdUseCase).handle(any());
    verify(studentDtoMapper).toResponse(student);
  }

  @Test
  void givenValidId_whenDeleteStudent_thenReturns204() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    doNothing().when(deleteStudentUseCase).handle(any());

    // Act
    ResponseEntity<Void> response = controller.deleteStudent(studentId);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());

    verify(deleteStudentUseCase).handle(any());
  }

  @Test
  void givenValidQuery_whenSearchStudents_thenReturnsPagedResponse() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    UUID studentId1 = UUID.randomUUID();
    UUID studentId2 = UUID.randomUUID();

    Student student1 = new Student(studentId1, "Harry Potter", schoolId);
    Student student2 = new Student(studentId2, "Hermione Granger", schoolId);

    StudentResponse response1 = new StudentResponse(studentId1, "Harry Potter", schoolId);
    StudentResponse response2 = new StudentResponse(studentId2, "Hermione Granger", schoolId);

    PagedResponse<Student> pagedResponse =
        new PagedResponse<>(List.of(student1, student2), 0, 20, 2, 1, false, false);

    when(searchStudentsUseCase.handle(any())).thenReturn(pagedResponse);
    when(studentDtoMapper.toResponse(student1)).thenReturn(response1);
    when(studentDtoMapper.toResponse(student2)).thenReturn(response2);

    // Act
    ResponseEntity<PagedResponse<StudentResponse>> response =
        controller.searchStudents(schoolId, "Harry", 0, 20);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().content().size());
    assertEquals(0, response.getBody().pageNumber());
    assertEquals(20, response.getBody().pageSize());
    assertEquals(2, response.getBody().totalElements());
    assertEquals(1, response.getBody().totalPages());
    assertFalse(response.getBody().hasNext());
    assertFalse(response.getBody().hasPrevious());

    verify(searchStudentsUseCase).handle(any());
    verify(studentDtoMapper, times(2)).toResponse(any());
  }

  @Test
  void givenEmptyQuery_whenSearchStudents_thenReturnsAllStudents() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    UUID studentId = UUID.randomUUID();
    Student student = new Student(studentId, "Harry Potter", schoolId);
    StudentResponse expectedResponse = new StudentResponse(studentId, "Harry Potter", schoolId);

    PagedResponse<Student> pagedResponse =
        new PagedResponse<>(List.of(student), 0, 20, 1, 1, false, false);

    when(searchStudentsUseCase.handle(any())).thenReturn(pagedResponse);
    when(studentDtoMapper.toResponse(student)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<PagedResponse<StudentResponse>> response =
        controller.searchStudents(schoolId, "", 0, 20);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().content().size());
    assertFalse(response.getBody().hasNext());
    assertFalse(response.getBody().hasPrevious());

    verify(searchStudentsUseCase).handle(any());
    verify(studentDtoMapper).toResponse(student);
  }

  @Test
  void givenPagedResults_whenSearchStudents_thenReturnsCorrectPagingFlags() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    UUID studentId1 = UUID.randomUUID();
    UUID studentId2 = UUID.randomUUID();

    Student student1 = new Student(studentId1, "Harry Potter", schoolId);
    Student student2 = new Student(studentId2, "Hermione Granger", schoolId);

    StudentResponse response1 = new StudentResponse(studentId1, "Harry Potter", schoolId);
    StudentResponse response2 = new StudentResponse(studentId2, "Hermione Granger", schoolId);

    // Simulate page 0 of 2 pages (hasNext = true, hasPrevious = false)
    PagedResponse<Student> pagedResponse =
        new PagedResponse<>(List.of(student1, student2), 0, 20, 40, 2, true, false);

    when(searchStudentsUseCase.handle(any())).thenReturn(pagedResponse);
    when(studentDtoMapper.toResponse(student1)).thenReturn(response1);
    when(studentDtoMapper.toResponse(student2)).thenReturn(response2);

    // Act
    ResponseEntity<PagedResponse<StudentResponse>> response =
        controller.searchStudents(schoolId, "Harry", 0, 20);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().content().size());
    assertEquals(0, response.getBody().pageNumber());
    assertEquals(20, response.getBody().pageSize());
    assertEquals(40, response.getBody().totalElements());
    assertEquals(2, response.getBody().totalPages());
    assertTrue(response.getBody().hasNext());
    assertFalse(response.getBody().hasPrevious());

    verify(searchStudentsUseCase).handle(any());
    verify(studentDtoMapper, times(2)).toResponse(any());
  }

  // Negative path tests

  @Test
  void givenNonExistentId_whenGetStudentById_thenThrowsNotFoundException() {
    // Arrange
    UUID nonExistentId = UUID.randomUUID();
    when(getStudentByIdUseCase.handle(any())).thenThrow(new NotFoundException("Student not found"));

    // Act & Assert
    assertThrows(
        NotFoundException.class,
        () -> {
          controller.getStudentById(nonExistentId);
        });

    verify(getStudentByIdUseCase).handle(any());
    verify(studentDtoMapper, never()).toResponse(any());
  }

  @Test
  void givenOverCapacity_whenCreateStudent_thenThrowsCapacityExceededException() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("Harry Potter", schoolId);
    CreateStudentCommand expectedCommand = new CreateStudentCommand("Harry Potter", schoolId);

    when(studentDtoMapper.toCreateCommand(request)).thenReturn(expectedCommand);
    when(createStudentUseCase.handle(expectedCommand))
        .thenThrow(new CapacityExceededException(schoolId));

    // Act & Assert
    assertThrows(
        CapacityExceededException.class,
        () -> {
          controller.createStudent(request);
        });

    verify(studentDtoMapper).toCreateCommand(request);
    verify(createStudentUseCase).handle(expectedCommand);
    verify(getStudentByIdUseCase, never()).handle(any());
    verify(studentDtoMapper, never()).toResponse(any());
  }

  @Test
  void givenNonExistentId_whenUpdateStudent_thenThrowsNotFoundException() {
    // Arrange
    UUID nonExistentId = UUID.randomUUID();
    String newName = "Updated Student Name";
    UpdateStudentCommand expectedCommand = new UpdateStudentCommand(nonExistentId, newName);

    when(studentDtoMapper.toUpdateCommand(nonExistentId, newName)).thenReturn(expectedCommand);
    doThrow(new NotFoundException("Student not found"))
        .when(updateStudentUseCase)
        .handle(expectedCommand);

    // Act & Assert
    assertThrows(
        NotFoundException.class,
        () -> {
          controller.updateStudent(nonExistentId, newName);
        });

    verify(studentDtoMapper).toUpdateCommand(nonExistentId, newName);
    verify(updateStudentUseCase).handle(expectedCommand);
    verify(getStudentByIdUseCase, never()).handle(any());
    verify(studentDtoMapper, never()).toResponse(any());
  }

  @Test
  void givenNonExistentId_whenDeleteStudent_thenThrowsNotFoundException() {
    // Arrange
    UUID nonExistentId = UUID.randomUUID();
    doThrow(new NotFoundException("Student not found")).when(deleteStudentUseCase).handle(any());

    // Act & Assert
    assertThrows(
        NotFoundException.class,
        () -> {
          controller.deleteStudent(nonExistentId);
        });

    verify(deleteStudentUseCase).handle(any());
  }

  @Test
  void givenNonExistentSchoolId_whenCreateStudent_thenThrowsNotFoundException() {
    // Arrange
    UUID nonExistentSchoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("Harry Potter", nonExistentSchoolId);
    CreateStudentCommand expectedCommand =
        new CreateStudentCommand("Harry Potter", nonExistentSchoolId);

    when(studentDtoMapper.toCreateCommand(request)).thenReturn(expectedCommand);
    when(createStudentUseCase.handle(expectedCommand))
        .thenThrow(new NotFoundException("School not found"));

    // Act & Assert
    assertThrows(
        NotFoundException.class,
        () -> {
          controller.createStudent(request);
        });

    verify(studentDtoMapper).toCreateCommand(request);
    verify(createStudentUseCase).handle(expectedCommand);
    verify(getStudentByIdUseCase, never()).handle(any());
    verify(studentDtoMapper, never()).toResponse(any());
  }

  // Helper methods for creating test data
  private Student createTestStudent(UUID id, String name, UUID schoolId) {
    return new Student(id, name, schoolId);
  }

  private StudentRequest createTestStudentRequest(String name, UUID schoolId) {
    return new StudentRequest(name, schoolId);
  }

  private StudentResponse createTestStudentResponse(UUID id, String name, UUID schoolId) {
    return new StudentResponse(id, name, schoolId);
  }
}

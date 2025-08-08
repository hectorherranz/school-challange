package com.hectorherranz.schoolapi.adapters.in.rest.school;

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
class SchoolStudentControllerTest {

  @Mock private CreateStudentUseCase createStudentUseCase;

  @Mock private GetStudentByIdUseCase getStudentByIdUseCase;

  @Mock private UpdateStudentUseCase updateStudentUseCase;

  @Mock private DeleteStudentUseCase deleteStudentUseCase;

  @Mock private SearchStudentsUseCase searchStudentsUseCase;

  @Mock private StudentDtoMapper studentDtoMapper;

  private SchoolStudentController controller;

  @BeforeEach
  void setUp() {
    controller =
        new SchoolStudentController(
            createStudentUseCase,
            getStudentByIdUseCase,
            updateStudentUseCase,
            deleteStudentUseCase,
            searchStudentsUseCase,
            studentDtoMapper);
  }

  @Test
  void givenValidRequest_whenEnrollStudent_thenReturns201WithLocation() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("Harry Potter");
    CreateStudentCommand expectedCommand = new CreateStudentCommand("Harry Potter", schoolId);
    Student student = new Student(studentId, "Harry Potter", schoolId);
    StudentResponse expectedResponse = new StudentResponse(studentId, "Harry Potter", schoolId);

    when(studentDtoMapper.toCreateCommand(request, schoolId)).thenReturn(expectedCommand);
    when(createStudentUseCase.handle(expectedCommand)).thenReturn(studentId);
    when(getStudentByIdUseCase.handle(any())).thenReturn(student);
    when(studentDtoMapper.toResponse(student)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<StudentResponse> response = controller.enrollStudent(schoolId, request);

    // Assert
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().id());
    assertEquals("Harry Potter", response.getBody().name());
    assertEquals(schoolId, response.getBody().schoolId());

    verify(studentDtoMapper).toCreateCommand(request, schoolId);
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
    ResponseEntity<StudentResponse> response = controller.getStudentById(schoolId, studentId);

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
  void givenStudentFromDifferentSchool_whenGetStudentById_thenThrowsNotFoundException() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();

    when(getStudentByIdUseCase.handle(any()))
        .thenThrow(new NotFoundException("Student", "Student not found in school"));

    // Act & Assert
    assertThrows(NotFoundException.class, () -> controller.getStudentById(schoolId, studentId));

    verify(getStudentByIdUseCase).handle(any());
    verify(studentDtoMapper, never()).toResponse(any());
  }

  @Test
  void givenValidRequest_whenUpdateStudent_thenReturns200() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("Updated Harry Potter");
    UpdateStudentCommand expectedCommand =
        new UpdateStudentCommand(schoolId, studentId, "Updated Harry Potter");
    Student student = new Student(studentId, "Updated Harry Potter", schoolId);
    StudentResponse expectedResponse =
        new StudentResponse(studentId, "Updated Harry Potter", schoolId);

    when(studentDtoMapper.toUpdateCommand(request, schoolId, studentId))
        .thenReturn(expectedCommand);
    when(getStudentByIdUseCase.handle(any())).thenReturn(student);
    when(studentDtoMapper.toResponse(student)).thenReturn(expectedResponse);

    // Act
    ResponseEntity<StudentResponse> response =
        controller.updateStudent(schoolId, studentId, request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(studentId, response.getBody().id());
    assertEquals("Updated Harry Potter", response.getBody().name());
    assertEquals(schoolId, response.getBody().schoolId());

    verify(studentDtoMapper).toUpdateCommand(request, schoolId, studentId);
    verify(updateStudentUseCase).handle(expectedCommand);
    verify(getStudentByIdUseCase).handle(any());
    verify(studentDtoMapper).toResponse(student);
  }

  @Test
  void givenValidId_whenRemoveStudent_thenReturns204() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();

    // Act
    ResponseEntity<Void> response = controller.removeStudent(schoolId, studentId);

    // Assert
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    assertNull(response.getBody());
  }

  @Test
  void givenValidQuery_whenListStudents_thenReturnsPagedResponse() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String query = "Harry";
    int page = 0;
    int size = 20;

    List<Student> students =
        List.of(
            createTestStudent(UUID.randomUUID(), "Harry Potter", schoolId),
            createTestStudent(UUID.randomUUID(), "Harry Styles", schoolId));

    PagedResponse<Student> expectedPagedResponse =
        new PagedResponse<>(students, page, size, 2L, 1, false, false);

    when(searchStudentsUseCase.handle(any())).thenReturn(expectedPagedResponse);
    when(studentDtoMapper.toResponse(any(Student.class)))
        .thenAnswer(
            invocation -> {
              Student student = invocation.getArgument(0);
              return createTestStudentResponse(student.id(), student.name(), student.schoolId());
            });

    // Act
    ResponseEntity<PagedResponse<StudentResponse>> response =
        controller.listStudents(schoolId, query, page, size);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().content().size());
    assertEquals(page, response.getBody().pageNumber());
    assertEquals(size, response.getBody().pageSize());
    assertEquals(2L, response.getBody().totalElements());

    verify(searchStudentsUseCase).handle(any());
  }

  @Test
  void givenEmptyQuery_whenListStudents_thenReturnsAllStudents() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String query = "";
    int page = 0;
    int size = 20;

    List<Student> students =
        List.of(
            createTestStudent(UUID.randomUUID(), "Harry Potter", schoolId),
            createTestStudent(UUID.randomUUID(), "Hermione Granger", schoolId));

    PagedResponse<Student> expectedPagedResponse =
        new PagedResponse<>(students, page, size, 2L, 1, false, false);

    when(searchStudentsUseCase.handle(any())).thenReturn(expectedPagedResponse);
    when(studentDtoMapper.toResponse(any(Student.class)))
        .thenAnswer(
            invocation -> {
              Student student = invocation.getArgument(0);
              return createTestStudentResponse(student.id(), student.name(), student.schoolId());
            });

    // Act
    ResponseEntity<PagedResponse<StudentResponse>> response =
        controller.listStudents(schoolId, query, page, size);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(2, response.getBody().content().size());

    verify(searchStudentsUseCase).handle(any());
  }

  @Test
  void givenPagedResults_whenListStudents_thenReturnsCorrectPagingFlags() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String query = "";
    int page = 1;
    int size = 10;

    List<Student> students =
        List.of(
            createTestStudent(UUID.randomUUID(), "Student 1", schoolId),
            createTestStudent(UUID.randomUUID(), "Student 2", schoolId));

    PagedResponse<Student> expectedPagedResponse =
        new PagedResponse<>(students, page, size, 25L, 3, true, true);

    when(searchStudentsUseCase.handle(any())).thenReturn(expectedPagedResponse);
    when(studentDtoMapper.toResponse(any(Student.class)))
        .thenAnswer(
            invocation -> {
              Student student = invocation.getArgument(0);
              return createTestStudentResponse(student.id(), student.name(), student.schoolId());
            });

    // Act
    ResponseEntity<PagedResponse<StudentResponse>> response =
        controller.listStudents(schoolId, query, page, size);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(page, response.getBody().pageNumber());
    assertEquals(size, response.getBody().pageSize());
    assertEquals(25L, response.getBody().totalElements());
    assertEquals(3, response.getBody().totalPages());
    assertTrue(response.getBody().hasNext());
    assertTrue(response.getBody().hasPrevious());

    verify(searchStudentsUseCase).handle(any());
  }

  @Test
  void givenNonExistentId_whenGetStudentById_thenThrowsNotFoundException() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();

    when(getStudentByIdUseCase.handle(any()))
        .thenThrow(new NotFoundException("Student", studentId.toString()));

    // Act & Assert
    assertThrows(NotFoundException.class, () -> controller.getStudentById(schoolId, studentId));

    verify(getStudentByIdUseCase).handle(any());
    verify(studentDtoMapper, never()).toResponse(any());
  }

  @Test
  void givenOverCapacity_whenEnrollStudent_thenThrowsCapacityExceededException() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("Harry Potter");
    CreateStudentCommand expectedCommand = new CreateStudentCommand("Harry Potter", schoolId);

    when(studentDtoMapper.toCreateCommand(request, schoolId)).thenReturn(expectedCommand);
    when(createStudentUseCase.handle(expectedCommand))
        .thenThrow(new CapacityExceededException(schoolId));

    // Act & Assert
    assertThrows(
        CapacityExceededException.class, () -> controller.enrollStudent(schoolId, request));

    verify(studentDtoMapper).toCreateCommand(request, schoolId);
    verify(createStudentUseCase).handle(expectedCommand);
    verify(getStudentByIdUseCase, never()).handle(any());
    verify(studentDtoMapper, never()).toResponse(any());
  }

  @Test
  void givenNonExistentId_whenUpdateStudent_thenThrowsNotFoundException() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("Updated Harry Potter");
    UpdateStudentCommand expectedCommand =
        new UpdateStudentCommand(schoolId, studentId, "Updated Harry Potter");

    when(studentDtoMapper.toUpdateCommand(request, schoolId, studentId))
        .thenReturn(expectedCommand);
    doThrow(new NotFoundException("Student", studentId.toString()))
        .when(updateStudentUseCase)
        .handle(expectedCommand);

    // Act & Assert
    assertThrows(
        NotFoundException.class, () -> controller.updateStudent(schoolId, studentId, request));

    verify(studentDtoMapper).toUpdateCommand(request, schoolId, studentId);
    verify(updateStudentUseCase).handle(expectedCommand);
    verify(getStudentByIdUseCase, never()).handle(any());
    verify(studentDtoMapper, never()).toResponse(any());
  }

  @Test
  void givenNonExistentId_whenRemoveStudent_thenThrowsNotFoundException() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();

    doThrow(new NotFoundException("Student", studentId.toString()))
        .when(deleteStudentUseCase)
        .handle(any());

    // Act & Assert
    assertThrows(NotFoundException.class, () -> controller.removeStudent(schoolId, studentId));

    verify(deleteStudentUseCase).handle(any());
  }

  @Test
  void givenNonExistentSchoolId_whenEnrollStudent_thenThrowsNotFoundException() {
    // Arrange
    UUID nonExistentSchoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("Harry Potter");
    CreateStudentCommand expectedCommand =
        new CreateStudentCommand("Harry Potter", nonExistentSchoolId);

    when(studentDtoMapper.toCreateCommand(request, nonExistentSchoolId))
        .thenReturn(expectedCommand);
    when(createStudentUseCase.handle(expectedCommand))
        .thenThrow(new NotFoundException("School", nonExistentSchoolId.toString()));

    // Act & Assert
    assertThrows(
        NotFoundException.class, () -> controller.enrollStudent(nonExistentSchoolId, request));

    verify(studentDtoMapper).toCreateCommand(request, nonExistentSchoolId);
    verify(createStudentUseCase).handle(expectedCommand);
    verify(getStudentByIdUseCase, never()).handle(any());
    verify(studentDtoMapper, never()).toResponse(any());
  }

  // Helper methods
  private Student createTestStudent(UUID id, String name, UUID schoolId) {
    return new Student(id, name, schoolId);
  }

  private StudentRequest createTestStudentRequest(String name) {
    return new StudentRequest(name);
  }

  private StudentResponse createTestStudentResponse(UUID id, String name, UUID schoolId) {
    return new StudentResponse(id, name, schoolId);
  }
}

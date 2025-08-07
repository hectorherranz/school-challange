package com.hectorherranz.schoolapi.adapters.in.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentResponse;
import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;
import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.domain.model.Student;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentDtoMapperTest {

  private StudentDtoMapper mapper;
  private UUID studentId;
  private UUID schoolId;
  private String name;

  @BeforeEach
  void setUp() {
    mapper = new StudentDtoMapper();
    studentId = UUID.randomUUID();
    schoolId = UUID.randomUUID();
    name = "Harry Potter";
  }

  @Test
  void shouldMapToCreateCommand() {
    // Given
    StudentRequest request = new StudentRequest(name);

    // When
    CreateStudentCommand command = mapper.toCreateCommand(request, schoolId);

    // Then
    assertNotNull(command);
    assertEquals(name, command.name());
    assertEquals(schoolId, command.schoolId());
  }

  @Test
  void shouldMapToUpdateCommand() {
    // Given
    String newName = "Updated Harry Potter";

    // When
    UpdateStudentCommand command = mapper.toUpdateCommand(schoolId, studentId, newName);

    // Then
    assertNotNull(command);
    assertEquals(schoolId, command.schoolId());
    assertEquals(studentId, command.studentId());
    assertEquals(newName, command.name());
  }

  @Test
  void shouldMapToResponse() {
    // Given
    Student student = new Student(studentId, name, schoolId);

    // When
    StudentResponse response = mapper.toResponse(student);

    // Then
    assertNotNull(response);
    assertEquals(studentId, response.id());
    assertEquals(name, response.name());
    assertEquals(schoolId, response.schoolId());
  }

  @Test
  void shouldHandleNullValues() {
    // Given
    Student nullStudent = null;

    // When & Then
    assertThrows(NullPointerException.class, () -> mapper.toResponse(nullStudent));
  }

  @Test
  void shouldPreserveAllFieldsInMapping() {
    // Given
    UUID customStudentId = UUID.randomUUID();
    UUID customSchoolId = UUID.randomUUID();
    String customName = "Hermione Granger";
    Student student = new Student(customStudentId, customName, customSchoolId);

    // When
    StudentResponse response = mapper.toResponse(student);

    // Then
    assertEquals(customStudentId, response.id());
    assertEquals(customName, response.name());
    assertEquals(customSchoolId, response.schoolId());
  }
}

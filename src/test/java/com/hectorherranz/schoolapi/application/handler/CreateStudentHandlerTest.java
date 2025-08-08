package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.adapters.out.jpa.service.StudentInfrastructureService;
import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;
import com.hectorherranz.schoolapi.domain.exception.CapacityExceededException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateStudentHandlerTest {

  @Mock private SchoolRepositoryPort schoolRepository;
  @Mock private StudentInfrastructureService studentInfrastructureService;

  private CreateStudentHandler handler;

  @BeforeEach
  void setUp() {
    handler = new CreateStudentHandler(schoolRepository, studentInfrastructureService);
  }

  @Test
  void handle_ValidCommand_ReturnsStudentId() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String studentName = "Harry Potter";
    CreateStudentCommand command = new CreateStudentCommand(studentName, schoolId);

    School school = new School(schoolId, "Hogwarts", new Capacity(500));
    Student student = new Student(UUID.randomUUID(), studentName, schoolId);

    when(schoolRepository.findByIdBasic(schoolId)).thenReturn(Optional.of(school));
    when(schoolRepository.countStudentsBySchoolId(schoolId)).thenReturn(100);
    when(studentInfrastructureService.createStudent(any(Student.class))).thenReturn(student);

    // Act
    UUID result = handler.handle(command);

    // Assert
    assertNotNull(result);
    verify(schoolRepository).findByIdBasic(schoolId);
    verify(schoolRepository).countStudentsBySchoolId(schoolId);
    verify(studentInfrastructureService).createStudent(any(Student.class));
  }

  @Test
  void handle_SchoolNotFound_ThrowsNotFoundException() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String studentName = "Harry Potter";
    CreateStudentCommand command = new CreateStudentCommand(studentName, schoolId);

    when(schoolRepository.findByIdBasic(schoolId)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> handler.handle(command));

    assertEquals("School not found with identifier: " + schoolId, exception.getMessage());
    verify(schoolRepository).findByIdBasic(schoolId);
    verify(schoolRepository, never()).countStudentsBySchoolId(any());
    verify(studentInfrastructureService, never()).createStudent(any());
  }

  @Test
  void handle_CapacityExceeded_ThrowsCapacityExceededException() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String studentName = "Harry Potter";
    CreateStudentCommand command = new CreateStudentCommand(studentName, schoolId);

    School school = new School(schoolId, "Hogwarts", new Capacity(100));

    when(schoolRepository.findByIdBasic(schoolId)).thenReturn(Optional.of(school));
    when(schoolRepository.countStudentsBySchoolId(schoolId)).thenReturn(100);

    // Act & Assert
    CapacityExceededException exception =
        assertThrows(CapacityExceededException.class, () -> handler.handle(command));

    assertEquals("School " + schoolId + " is at maximum capacity", exception.getMessage());
    verify(schoolRepository).findByIdBasic(schoolId);
    verify(schoolRepository).countStudentsBySchoolId(schoolId);
    verify(studentInfrastructureService, never()).createStudent(any());
  }
}

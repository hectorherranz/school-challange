package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
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

  private CreateStudentHandler handler;

  @BeforeEach
  void setUp() {
    handler = new CreateStudentHandler(schoolRepository);
  }

  @Test
  void handle_ValidCommand_ReturnsStudentId() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String studentName = "Harry Potter";
    CreateStudentCommand command = new CreateStudentCommand(studentName, schoolId);

    School school = new School(schoolId, "Hogwarts", new Capacity(500));

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(school));
    when(schoolRepository.save(any(School.class))).thenReturn(school);

    // Act
    UUID result = handler.handle(command);

    // Assert
    assertNotNull(result);
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository).save(school);
  }

  @Test
  void handle_SchoolNotFound_ThrowsNotFoundException() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String studentName = "Harry Potter";
    CreateStudentCommand command = new CreateStudentCommand(studentName, schoolId);

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> handler.handle(command));

    assertEquals("School not found with identifier: " + schoolId, exception.getMessage());
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository, never()).save(any(School.class));
  }

  @Test
  void handle_ValidCommand_SavesUpdatedSchool() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String studentName = "Ron Weasley";
    CreateStudentCommand command = new CreateStudentCommand(studentName, schoolId);

    School school = new School(schoolId, "Hogwarts", new Capacity(500));

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(school));
    when(schoolRepository.save(any(School.class))).thenReturn(school);

    // Act
    handler.handle(command);

    // Assert
    verify(schoolRepository).save(school);
  }

  @Test
  void handle_ValidCommand_EnrollsStudentThroughSchoolAggregate() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String studentName = "Neville Longbottom";
    CreateStudentCommand command = new CreateStudentCommand(studentName, schoolId);

    School school = new School(schoolId, "Hogwarts", new Capacity(500));

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(school));
    when(schoolRepository.save(any(School.class))).thenReturn(school);

    // Act
    UUID result = handler.handle(command);

    // Assert
    assertNotNull(result);
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository).save(school);
  }
}

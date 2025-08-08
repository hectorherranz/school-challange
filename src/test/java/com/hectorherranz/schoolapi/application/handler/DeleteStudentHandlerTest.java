package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.application.command.DeleteStudentCommand;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteStudentHandlerTest {

  @Mock private StudentRepositoryPort studentRepository;

  private DeleteStudentHandler handler;

  @BeforeEach
  void setUp() {
    handler = new DeleteStudentHandler(studentRepository);
  }

  @Test
  void shouldDeleteStudentSuccessfully() {
    // Given
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    DeleteStudentCommand command = new DeleteStudentCommand(schoolId, studentId);

    when(studentRepository.existsByIdAndSchoolId(studentId, schoolId)).thenReturn(true);

    // When
    handler.handle(command);

    // Then
    verify(studentRepository).existsByIdAndSchoolId(studentId, schoolId);
    verify(studentRepository).deleteById(studentId);
  }

  @Test
  void shouldThrowNotFoundExceptionWhenStudentNotFoundInSchool() {
    // Given
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    DeleteStudentCommand command = new DeleteStudentCommand(schoolId, studentId);

    when(studentRepository.existsByIdAndSchoolId(studentId, schoolId)).thenReturn(false);

    // When & Then
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> handler.handle(command));

    assertEquals(
        "Student not found with identifier: Student "
            + studentId
            + " not found in school "
            + schoolId,
        exception.getMessage());
    verify(studentRepository).existsByIdAndSchoolId(studentId, schoolId);
    verify(studentRepository, never()).deleteById(any());
  }
}

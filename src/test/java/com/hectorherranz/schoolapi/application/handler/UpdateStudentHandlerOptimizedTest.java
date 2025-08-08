package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.adapters.out.jpa.service.StudentInfrastructureService;
import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateStudentHandlerOptimizedTest {

  @Mock private StudentInfrastructureService infrastructureService;

  private UpdateStudentHandlerOptimized handler;

  @BeforeEach
  void setUp() {
    handler = new UpdateStudentHandlerOptimized(infrastructureService);
  }

  @Test
  void shouldUpdateStudentSuccessfully() {
    // Given
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    String newName = "Updated Harry Potter";
    UpdateStudentCommand command = new UpdateStudentCommand(schoolId, studentId, newName);

    // When
    handler.handle(command);

    // Then
    verify(infrastructureService).updateStudentOptimized(studentId, schoolId, newName);
  }

  @Test
  void shouldPropagateNotFoundException() {
    // Given
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    String newName = "Updated Harry Potter";
    UpdateStudentCommand command = new UpdateStudentCommand(schoolId, studentId, newName);

    doThrow(new NotFoundException("Student", "Student not found"))
        .when(infrastructureService)
        .updateStudentOptimized(studentId, schoolId, newName);

    // When & Then
    assertThrows(NotFoundException.class, () -> handler.handle(command));
    verify(infrastructureService).updateStudentOptimized(studentId, schoolId, newName);
  }
}

package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.adapters.out.jpa.service.SchoolInfrastructureService;
import com.hectorherranz.schoolapi.application.command.UpdateSchoolCommand;
import com.hectorherranz.schoolapi.domain.exception.DuplicateNameException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateSchoolHandlerOptimizedTest {

  @Mock private SchoolInfrastructureService infrastructureService;

  private UpdateSchoolHandlerOptimized handler;

  @BeforeEach
  void setUp() {
    handler = new UpdateSchoolHandlerOptimized(infrastructureService);
  }

  @Test
  void shouldUpdateSchoolNameSuccessfully() {
    // Given
    UUID schoolId = UUID.randomUUID();
    String newName = "Updated School Name";
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.of(newName), Optional.empty());

    // When
    handler.handle(command);

    // Then
    verify(infrastructureService)
        .updateSchoolOptimized(schoolId, Optional.of(newName), Optional.empty());
  }

  @Test
  void shouldUpdateSchoolCapacitySuccessfully() {
    // Given
    UUID schoolId = UUID.randomUUID();
    int newCapacity = 1000;
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.empty(), Optional.of(newCapacity));

    // When
    handler.handle(command);

    // Then
    verify(infrastructureService)
        .updateSchoolOptimized(schoolId, Optional.empty(), Optional.of(newCapacity));
  }

  @Test
  void shouldUpdateSchoolNameAndCapacitySuccessfully() {
    // Given
    UUID schoolId = UUID.randomUUID();
    String newName = "Updated School Name";
    int newCapacity = 1000;
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.of(newName), Optional.of(newCapacity));

    // When
    handler.handle(command);

    // Then
    verify(infrastructureService)
        .updateSchoolOptimized(schoolId, Optional.of(newName), Optional.of(newCapacity));
  }

  @Test
  void shouldPropagateNotFoundException() {
    // Given
    UUID schoolId = UUID.randomUUID();
    String newName = "Updated School Name";
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.of(newName), Optional.empty());

    doThrow(new NotFoundException("School", "School not found"))
        .when(infrastructureService)
        .updateSchoolOptimized(schoolId, Optional.of(newName), Optional.empty());

    // When & Then
    assertThrows(NotFoundException.class, () -> handler.handle(command));
    verify(infrastructureService)
        .updateSchoolOptimized(schoolId, Optional.of(newName), Optional.empty());
  }

  @Test
  void shouldPropagateDuplicateNameException() {
    // Given
    UUID schoolId = UUID.randomUUID();
    String newName = "Updated School Name";
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.of(newName), Optional.empty());

    doThrow(new DuplicateNameException())
        .when(infrastructureService)
        .updateSchoolOptimized(schoolId, Optional.of(newName), Optional.empty());

    // When & Then
    assertThrows(DuplicateNameException.class, () -> handler.handle(command));
    verify(infrastructureService)
        .updateSchoolOptimized(schoolId, Optional.of(newName), Optional.empty());
  }
}

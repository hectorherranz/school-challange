package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.application.command.UpdateSchoolCommand;
import com.hectorherranz.schoolapi.application.port.out.SchoolRepositoryPort;
import com.hectorherranz.schoolapi.domain.exception.DuplicateNameException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateSchoolHandlerTest {

  @Mock private SchoolRepositoryPort schoolRepository;

  private UpdateSchoolHandler handler;

  @BeforeEach
  void setUp() {
    handler = new UpdateSchoolHandler(schoolRepository);
  }

  @Test
  void handle_UpdateNameOnly_UpdatesSchoolName() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String oldName = "Old School Name";
    String newName = "New School Name";
    int capacity = 500;

    School existingSchool = new School(schoolId, oldName, new Capacity(capacity));
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.of(newName), Optional.empty());

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(existingSchool));
    when(schoolRepository.existsByNameIgnoreCase(newName)).thenReturn(false);
    when(schoolRepository.save(any(School.class))).thenReturn(existingSchool);

    // Act
    handler.handle(command);

    // Assert
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository).existsByNameIgnoreCase(newName);
    verify(schoolRepository).save(existingSchool);
  }

  @Test
  void handle_UpdateCapacityOnly_UpdatesSchoolCapacity() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String name = "Test School";
    int oldCapacity = 500;
    int newCapacity = 600;

    School existingSchool = new School(schoolId, name, new Capacity(oldCapacity));
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.empty(), Optional.of(newCapacity));

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(existingSchool));
    when(schoolRepository.save(any(School.class))).thenReturn(existingSchool);

    // Act
    handler.handle(command);

    // Assert
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository).save(existingSchool);
  }

  @Test
  void handle_UpdateBothNameAndCapacity_UpdatesBoth() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String oldName = "Old School Name";
    String newName = "New School Name";
    int oldCapacity = 500;
    int newCapacity = 600;

    School existingSchool = new School(schoolId, oldName, new Capacity(oldCapacity));
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.of(newName), Optional.of(newCapacity));

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(existingSchool));
    when(schoolRepository.existsByNameIgnoreCase(newName)).thenReturn(false);
    when(schoolRepository.save(any(School.class))).thenReturn(existingSchool);

    // Act
    handler.handle(command);

    // Assert
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository).existsByNameIgnoreCase(newName);
    verify(schoolRepository).save(existingSchool);
  }

  @Test
  void handle_SchoolNotFound_ThrowsNotFoundException() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.of("New Name"), Optional.empty());

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> handler.handle(command));

    assertEquals("School not found with identifier: " + schoolId, exception.getMessage());
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository, never()).save(any(School.class));
  }

  @Test
  void handle_DuplicateName_ThrowsDuplicateNameException() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String oldName = "Old School Name";
    String newName = "Existing School Name";
    int capacity = 500;

    School existingSchool = new School(schoolId, oldName, new Capacity(capacity));
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.of(newName), Optional.empty());

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(existingSchool));
    when(schoolRepository.existsByNameIgnoreCase(newName)).thenReturn(true);

    // Act & Assert
    DuplicateNameException exception =
        assertThrows(DuplicateNameException.class, () -> handler.handle(command));

    assertEquals("A school with this name already exists", exception.getMessage());
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository).existsByNameIgnoreCase(newName);
    verify(schoolRepository, never()).save(any(School.class));
  }

  @Test
  void handle_SameName_DoesNotCheckForDuplicate() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String name = "Same School Name";
    int capacity = 500;

    School existingSchool = new School(schoolId, name, new Capacity(capacity));
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.of(name), Optional.empty());

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(existingSchool));
    when(schoolRepository.save(any(School.class))).thenReturn(existingSchool);

    // Act
    handler.handle(command);

    // Assert
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository, never()).existsByNameIgnoreCase(any());
    verify(schoolRepository).save(existingSchool);
  }

  @Test
  void handle_NoUpdates_DoesNotModifySchool() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String name = "Test School";
    int capacity = 500;

    School existingSchool = new School(schoolId, name, new Capacity(capacity));
    UpdateSchoolCommand command =
        new UpdateSchoolCommand(schoolId, Optional.empty(), Optional.empty());

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(existingSchool));
    when(schoolRepository.save(any(School.class))).thenReturn(existingSchool);

    // Act
    handler.handle(command);

    // Assert
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository).save(existingSchool);
  }
}

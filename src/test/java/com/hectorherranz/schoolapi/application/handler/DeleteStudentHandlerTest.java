package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.application.command.DeleteStudentCommand;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteStudentHandlerTest {

  @Mock private SchoolRepositoryPort schoolRepository;

  private DeleteStudentHandler handler;

  @BeforeEach
  void setUp() {
    handler = new DeleteStudentHandler(schoolRepository);
  }

  @Test
  void shouldDeleteStudentSuccessfully() {
    // Given
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    String studentName = "Harry Potter";

    Student existingStudent = new Student(studentId, studentName, schoolId);
    School school =
        School.rehydrate(schoolId, "Test School", new Capacity(100), List.of(existingStudent));
    DeleteStudentCommand command = new DeleteStudentCommand(schoolId, studentId);

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(school));
    when(schoolRepository.save(any(School.class))).thenReturn(school);

    // When
    handler.handle(command);

    // Then
    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository).save(any(School.class));
  }

  @Test
  void shouldThrowNotFoundExceptionWhenSchoolNotFound() {
    // Given
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    DeleteStudentCommand command = new DeleteStudentCommand(schoolId, studentId);

    when(schoolRepository.findById(schoolId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(NotFoundException.class, () -> handler.handle(command));

    verify(schoolRepository).findById(schoolId);
    verify(schoolRepository, never()).save(any());
  }
}

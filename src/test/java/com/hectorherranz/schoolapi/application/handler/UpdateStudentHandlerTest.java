package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateStudentHandlerTest {

  @Mock private StudentRepositoryPort studentRepository;

  private UpdateStudentHandler handler;

  @BeforeEach
  void setUp() {
    handler = new UpdateStudentHandler(studentRepository);
  }

  @Test
  void handle_ValidCommand_UpdatesStudentName() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    String oldName = "Old Student Name";
    String newName = "New Student Name";

    Student existingStudent = new Student(studentId, oldName, schoolId);
    UpdateStudentCommand command = new UpdateStudentCommand(studentId, newName);

    when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
    when(studentRepository.save(any(Student.class))).thenReturn(existingStudent);

    // Act
    handler.handle(command);

    // Assert
    verify(studentRepository).findById(studentId);
    verify(studentRepository)
        .save(
            argThat(
                student ->
                    student.id().equals(studentId)
                        && student.name().equals(newName)
                        && student.schoolId().equals(schoolId)));
  }

  @Test
  void handle_StudentNotFound_ThrowsNotFoundException() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    String newName = "New Student Name";
    UpdateStudentCommand command = new UpdateStudentCommand(studentId, newName);

    when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> handler.handle(command));

    assertEquals("Student not found with identifier: " + studentId, exception.getMessage());
    verify(studentRepository).findById(studentId);
    verify(studentRepository, never()).save(any(Student.class));
  }

  @Test
  void handle_ValidCommand_CreatesNewStudentInstance() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    String oldName = "Old Student Name";
    String newName = "New Student Name";

    Student existingStudent = new Student(studentId, oldName, schoolId);
    UpdateStudentCommand command = new UpdateStudentCommand(studentId, newName);

    when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
    when(studentRepository.save(any(Student.class)))
        .thenAnswer(
            invocation -> {
              Student savedStudent = invocation.getArgument(0);
              return savedStudent;
            });

    // Act
    handler.handle(command);

    // Assert
    verify(studentRepository)
        .save(
            argThat(
                student ->
                    student.id().equals(studentId)
                        && student.name().equals(newName)
                        && student.schoolId().equals(schoolId)));
  }

  @Test
  void handle_ValidCommand_PreservesSchoolId() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    String oldName = "Old Student Name";
    String newName = "New Student Name";

    Student existingStudent = new Student(studentId, oldName, schoolId);
    UpdateStudentCommand command = new UpdateStudentCommand(studentId, newName);

    when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
    when(studentRepository.save(any(Student.class))).thenReturn(existingStudent);

    // Act
    handler.handle(command);

    // Assert
    verify(studentRepository).save(argThat(student -> student.schoolId().equals(schoolId)));
  }

  @Test
  void handle_ValidCommand_UpdatesCorrectStudent() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    String newName = "Updated Student Name";

    Student existingStudent = new Student(studentId, "Old Name", UUID.randomUUID());
    UpdateStudentCommand command = new UpdateStudentCommand(studentId, newName);

    when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
    when(studentRepository.save(any(Student.class))).thenReturn(existingStudent);

    // Act
    handler.handle(command);

    // Assert
    verify(studentRepository).findById(studentId);
    verify(studentRepository).save(argThat(student -> student.id().equals(studentId)));
  }
}

package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.application.port.out.StudentRepositoryPort;
import com.hectorherranz.schoolapi.application.query.GetStudentByIdQuery;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.Student;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetStudentByIdHandlerTest {

  @Mock private StudentRepositoryPort studentRepository;

  private GetStudentByIdHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GetStudentByIdHandler(studentRepository);
  }

  @Test
  void handle_StudentExistsInSchool_ReturnsStudent() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    String studentName = "Harry Potter";
    UUID schoolId = UUID.randomUUID();

    Student expectedStudent = new Student(studentId, studentName, schoolId);
    GetStudentByIdQuery query = new GetStudentByIdQuery(studentId, schoolId);

    when(studentRepository.findByIdAndSchoolId(studentId, schoolId))
        .thenReturn(Optional.of(expectedStudent));

    // Act
    Student result = handler.handle(query);

    // Assert
    assertEquals(expectedStudent, result);
    assertEquals(studentId, result.id());
    assertEquals(studentName, result.name());
    assertEquals(schoolId, result.schoolId());
    verify(studentRepository).findByIdAndSchoolId(studentId, schoolId);
  }

  @Test
  void handle_StudentNotFoundInSchool_ThrowsNotFoundException() {
    // Arrange
    UUID studentId = UUID.randomUUID();
    UUID schoolId = UUID.randomUUID();
    GetStudentByIdQuery query = new GetStudentByIdQuery(studentId, schoolId);

    when(studentRepository.findByIdAndSchoolId(studentId, schoolId)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> handler.handle(query));

    assertEquals(
        "Student not found with identifier: Student "
            + studentId
            + " not found in school "
            + schoolId,
        exception.getMessage());
    verify(studentRepository).findByIdAndSchoolId(studentId, schoolId);
  }
}

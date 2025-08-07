package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.query.GetStudentByIdQuery;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetStudentByIdHandlerTest {

    @Mock
    private StudentRepositoryPort studentRepository;

    private GetStudentByIdHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetStudentByIdHandler(studentRepository);
    }

    @Test
    void handle_StudentExists_ReturnsStudent() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        String studentName = "Harry Potter";
        UUID schoolId = UUID.randomUUID();
        
        Student expectedStudent = new Student(studentId, studentName, schoolId);
        GetStudentByIdQuery query = new GetStudentByIdQuery(studentId);
        
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(expectedStudent));

        // Act
        Student result = handler.handle(query);

        // Assert
        assertEquals(expectedStudent, result);
        assertEquals(studentId, result.id());
        assertEquals(studentName, result.name());
        assertEquals(schoolId, result.schoolId());
        verify(studentRepository).findById(studentId);
    }

    @Test
    void handle_StudentNotFound_ThrowsNotFoundException() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        GetStudentByIdQuery query = new GetStudentByIdQuery(studentId);
        
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> handler.handle(query));
        
        assertEquals("Student not found with identifier: " + studentId, exception.getMessage());
        verify(studentRepository).findById(studentId);
    }

    @Test
    void handle_ValidQuery_ReturnsCorrectStudent() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        String studentName = "Hermione Granger";
        UUID schoolId = UUID.randomUUID();
        
        Student expectedStudent = new Student(studentId, studentName, schoolId);
        GetStudentByIdQuery query = new GetStudentByIdQuery(studentId);
        
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(expectedStudent));

        // Act
        Student result = handler.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(studentId, result.id());
        assertEquals(studentName, result.name());
        assertEquals(schoolId, result.schoolId());
    }

    @Test
    void handle_ValidQuery_CallsRepositoryWithCorrectId() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        GetStudentByIdQuery query = new GetStudentByIdQuery(studentId);
        
        Student expectedStudent = new Student(studentId, "Test Student", UUID.randomUUID());
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(expectedStudent));

        // Act
        handler.handle(query);

        // Assert
        verify(studentRepository).findById(studentId);
    }
}

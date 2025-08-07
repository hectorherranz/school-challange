package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.DeleteStudentCommand;
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
class DeleteStudentHandlerTest {

    @Mock
    private StudentRepositoryPort studentRepository;

    private DeleteStudentHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DeleteStudentHandler(studentRepository);
    }

    @Test
    void handle_StudentExists_DeletesStudent() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        String studentName = "Test Student";
        UUID schoolId = UUID.randomUUID();
        
        Student existingStudent = new Student(studentId, studentName, schoolId);
        DeleteStudentCommand command = new DeleteStudentCommand(studentId);
        
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        doNothing().when(studentRepository).deleteById(studentId);

        // Act
        handler.handle(command);

        // Assert
        verify(studentRepository).findById(studentId);
        verify(studentRepository).deleteById(studentId);
    }

    @Test
    void handle_StudentNotFound_ThrowsNotFoundException() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        DeleteStudentCommand command = new DeleteStudentCommand(studentId);
        
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> handler.handle(command));
        
        assertEquals("Student not found with identifier: " + studentId, exception.getMessage());
        verify(studentRepository).findById(studentId);
        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    void handle_ValidStudentId_DeletesCorrectStudent() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        DeleteStudentCommand command = new DeleteStudentCommand(studentId);
        
        Student existingStudent = new Student(studentId, "Test Student", UUID.randomUUID());
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        doNothing().when(studentRepository).deleteById(studentId);

        // Act
        handler.handle(command);

        // Assert
        verify(studentRepository).findById(studentId);
        verify(studentRepository).deleteById(studentId);
    }
}

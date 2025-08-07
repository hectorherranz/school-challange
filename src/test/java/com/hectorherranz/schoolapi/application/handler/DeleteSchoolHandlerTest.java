package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.DeleteSchoolCommand;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
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
class DeleteSchoolHandlerTest {

    @Mock
    private SchoolRepositoryPort schoolRepository;

    private DeleteSchoolHandler handler;

    @BeforeEach
    void setUp() {
        handler = new DeleteSchoolHandler(schoolRepository);
    }

    @Test
    void handle_SchoolExists_DeletesSchool() {
        // Arrange
        UUID schoolId = UUID.randomUUID();
        String schoolName = "Test School";
        int capacity = 500;
        
        School existingSchool = new School(schoolId, schoolName, new Capacity(capacity));
        DeleteSchoolCommand command = new DeleteSchoolCommand(schoolId);
        
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(existingSchool));
        doNothing().when(schoolRepository).deleteById(schoolId);

        // Act
        handler.handle(command);

        // Assert
        verify(schoolRepository).findById(schoolId);
        verify(schoolRepository).deleteById(schoolId);
    }

    @Test
    void handle_SchoolNotFound_ThrowsNotFoundException() {
        // Arrange
        UUID schoolId = UUID.randomUUID();
        DeleteSchoolCommand command = new DeleteSchoolCommand(schoolId);
        
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> handler.handle(command));
        
        assertEquals("School not found with identifier: " + schoolId, exception.getMessage());
        verify(schoolRepository).findById(schoolId);
        verify(schoolRepository, never()).deleteById(any());
    }

    @Test
    void handle_ValidSchoolId_DeletesCorrectSchool() {
        // Arrange
        UUID schoolId = UUID.randomUUID();
        DeleteSchoolCommand command = new DeleteSchoolCommand(schoolId);
        
        School existingSchool = new School(schoolId, "Test School", new Capacity(500));
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(existingSchool));
        doNothing().when(schoolRepository).deleteById(schoolId);

        // Act
        handler.handle(command);

        // Assert
        verify(schoolRepository).findById(schoolId);
        verify(schoolRepository).deleteById(schoolId);
    }
}

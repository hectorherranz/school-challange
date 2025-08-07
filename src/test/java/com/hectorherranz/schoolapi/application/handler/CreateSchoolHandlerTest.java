package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.CreateSchoolCommand;
import com.hectorherranz.schoolapi.domain.exception.DuplicateNameException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSchoolHandlerTest {

    @Mock
    private SchoolRepositoryPort schoolRepository;

    private CreateSchoolHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CreateSchoolHandler(schoolRepository);
    }

    @Test
    void handle_ValidCommand_ReturnsSchoolId() {
        // Arrange
        String schoolName = "Hogwarts School";
        int capacity = 500;
        CreateSchoolCommand command = new CreateSchoolCommand(schoolName, capacity);
        
        UUID expectedId = UUID.randomUUID();
        School savedSchool = new School(expectedId, schoolName, new Capacity(capacity));
        
        when(schoolRepository.existsByNameIgnoreCase(schoolName)).thenReturn(false);
        when(schoolRepository.save(any(School.class))).thenReturn(savedSchool);

        // Act
        UUID result = handler.handle(command);

        // Assert
        assertEquals(expectedId, result);
        verify(schoolRepository).existsByNameIgnoreCase(schoolName);
        verify(schoolRepository).save(any(School.class));
    }

    @Test
    void handle_DuplicateName_ThrowsDuplicateNameException() {
        // Arrange
        String schoolName = "Hogwarts School";
        int capacity = 500;
        CreateSchoolCommand command = new CreateSchoolCommand(schoolName, capacity);
        
        when(schoolRepository.existsByNameIgnoreCase(schoolName)).thenReturn(true);

        // Act & Assert
        DuplicateNameException exception = assertThrows(DuplicateNameException.class, 
            () -> handler.handle(command));
        
        assertEquals("A school with this name already exists", exception.getMessage());
        verify(schoolRepository).existsByNameIgnoreCase(schoolName);
        verify(schoolRepository, never()).save(any(School.class));
    }

    @Test
    void handle_ValidCommand_CreatesSchoolWithCorrectAttributes() {
        // Arrange
        String schoolName = "Beauxbatons Academy";
        int capacity = 300;
        CreateSchoolCommand command = new CreateSchoolCommand(schoolName, capacity);
        
        UUID expectedId = UUID.randomUUID();
        School savedSchool = new School(expectedId, schoolName, new Capacity(capacity));
        
        when(schoolRepository.existsByNameIgnoreCase(schoolName)).thenReturn(false);
        when(schoolRepository.save(any(School.class))).thenReturn(savedSchool);

        // Act
        UUID result = handler.handle(command);

        // Assert
        assertEquals(expectedId, result);
        verify(schoolRepository).save(argThat(school -> 
            school.name().equals(schoolName) && 
            school.capacity().value() == capacity
        ));
    }

    @Test
    void handle_ValidCommand_GeneratesNewUuid() {
        // Arrange
        String schoolName = "Durmstrang Institute";
        int capacity = 400;
        CreateSchoolCommand command = new CreateSchoolCommand(schoolName, capacity);
        
        when(schoolRepository.existsByNameIgnoreCase(schoolName)).thenReturn(false);
        when(schoolRepository.save(any(School.class))).thenAnswer(invocation -> {
            School school = invocation.getArgument(0);
            return school; // Return the school as-is to verify UUID generation
        });

        // Act
        UUID result = handler.handle(command);

        // Assert
        assertNotNull(result);
        verify(schoolRepository).save(argThat(school -> 
            school.id() != null && 
            school.name().equals(schoolName) && 
            school.capacity().value() == capacity
        ));
    }
}

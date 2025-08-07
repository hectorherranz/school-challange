package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.query.GetSchoolByIdQuery;
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
class GetSchoolByIdHandlerTest {

    @Mock
    private SchoolRepositoryPort schoolRepository;

    private GetSchoolByIdHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GetSchoolByIdHandler(schoolRepository);
    }

    @Test
    void handle_SchoolExists_ReturnsSchool() {
        // Arrange
        UUID schoolId = UUID.randomUUID();
        String schoolName = "Hogwarts School";
        int capacity = 500;
        
        School expectedSchool = new School(schoolId, schoolName, new Capacity(capacity));
        GetSchoolByIdQuery query = new GetSchoolByIdQuery(schoolId);
        
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(expectedSchool));

        // Act
        School result = handler.handle(query);

        // Assert
        assertEquals(expectedSchool, result);
        assertEquals(schoolId, result.id());
        assertEquals(schoolName, result.name());
        assertEquals(capacity, result.capacity().value());
        verify(schoolRepository).findById(schoolId);
    }

    @Test
    void handle_SchoolNotFound_ThrowsNotFoundException() {
        // Arrange
        UUID schoolId = UUID.randomUUID();
        GetSchoolByIdQuery query = new GetSchoolByIdQuery(schoolId);
        
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> handler.handle(query));
        
        assertEquals("School not found with identifier: " + schoolId, exception.getMessage());
        verify(schoolRepository).findById(schoolId);
    }

    @Test
    void handle_ValidQuery_ReturnsCorrectSchool() {
        // Arrange
        UUID schoolId = UUID.randomUUID();
        String schoolName = "Beauxbatons Academy";
        int capacity = 300;
        
        School expectedSchool = new School(schoolId, schoolName, new Capacity(capacity));
        GetSchoolByIdQuery query = new GetSchoolByIdQuery(schoolId);
        
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(expectedSchool));

        // Act
        School result = handler.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(schoolId, result.id());
        assertEquals(schoolName, result.name());
        assertEquals(capacity, result.capacity().value());
    }

    @Test
    void handle_ValidQuery_CallsRepositoryWithCorrectId() {
        // Arrange
        UUID schoolId = UUID.randomUUID();
        GetSchoolByIdQuery query = new GetSchoolByIdQuery(schoolId);
        
        School expectedSchool = new School(schoolId, "Test School", new Capacity(500));
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(expectedSchool));

        // Act
        handler.handle(query);

        // Assert
        verify(schoolRepository).findById(schoolId);
    }
}

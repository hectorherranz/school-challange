package com.hectorherranz.schoolapi.adapters.in.rest.mapper;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.StudentResponse;
import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;
import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.domain.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StudentDtoMapperTest {

    private StudentDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new StudentDtoMapper();
    }

    @Test
    void toCreateCommand_ValidRequest_ReturnsCorrectCommand() {
        // Arrange
        String name = "Harry Potter";
        UUID schoolId = UUID.randomUUID();
        StudentRequest request = new StudentRequest(name, schoolId);

        // Act
        CreateStudentCommand command = mapper.toCreateCommand(request);

        // Assert
        assertEquals(name, command.name());
        assertEquals(schoolId, command.schoolId());
    }

    @Test
    void toUpdateCommand_ValidParameters_ReturnsCorrectCommand() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        String newName = "Updated Student Name";

        // Act
        UpdateStudentCommand command = mapper.toUpdateCommand(studentId, newName);

        // Assert
        assertEquals(studentId, command.studentId());
        assertEquals(newName, command.name());
    }

    @Test
    void toResponse_ValidStudent_ReturnsCorrectResponse() {
        // Arrange
        UUID studentId = UUID.randomUUID();
        String name = "Harry Potter";
        UUID schoolId = UUID.randomUUID();
        Student student = new Student(studentId, name, schoolId);

        // Act
        StudentResponse response = mapper.toResponse(student);

        // Assert
        assertEquals(studentId, response.id());
        assertEquals(name, response.name());
        assertEquals(schoolId, response.schoolId());
    }
}

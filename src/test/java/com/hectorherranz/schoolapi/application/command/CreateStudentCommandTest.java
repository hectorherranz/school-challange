package com.hectorherranz.schoolapi.application.command;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateStudentCommandTest {

    private static Validator VALIDATOR;
    private static final UUID VALID_SCHOOL_ID = UUID.randomUUID();

    @BeforeAll
    static void setUp() {
        VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validCommand_passes() {
        CreateStudentCommand command = new CreateStudentCommand("Harry Potter", VALID_SCHOOL_ID);
        Set<ConstraintViolation<CreateStudentCommand>> violations = VALIDATOR.validate(command);
        
        assertEquals(0, violations.size());
    }

    @Test
    void blankName_fails() {
        CreateStudentCommand command = new CreateStudentCommand(" ", VALID_SCHOOL_ID);
        Set<ConstraintViolation<CreateStudentCommand>> violations = VALIDATOR.validate(command);
        
        assertEquals(1, violations.size());
        ConstraintViolation<CreateStudentCommand> violation = violations.iterator().next();
        assertEquals("must not be blank", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void nullSchoolId_fails() {
        CreateStudentCommand command = new CreateStudentCommand("Harry Potter", null);
        Set<ConstraintViolation<CreateStudentCommand>> violations = VALIDATOR.validate(command);
        
        assertEquals(1, violations.size());
        ConstraintViolation<CreateStudentCommand> violation = violations.iterator().next();
        assertEquals("must not be null", violation.getMessage());
        assertEquals("schoolId", violation.getPropertyPath().toString());
    }

    @Test
    void bothInvalidFields_fails() {
        CreateStudentCommand command = new CreateStudentCommand(" ", null);
        Set<ConstraintViolation<CreateStudentCommand>> violations = VALIDATOR.validate(command);
        
        assertEquals(2, violations.size());
        boolean hasNameViolation = violations.stream()
                .anyMatch(violation -> "name".equals(violation.getPropertyPath().toString()));
        boolean hasSchoolIdViolation = violations.stream()
                .anyMatch(violation -> "schoolId".equals(violation.getPropertyPath().toString()));
        
        assertTrue(hasNameViolation);
        assertTrue(hasSchoolIdViolation);
    }
}

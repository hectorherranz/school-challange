package com.hectorherranz.schoolapi.application.command;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DeleteStudentCommandTest {

  private static Validator VALIDATOR;

  @BeforeAll
  static void setUp() {
    VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validCommand_passes() {
    DeleteStudentCommand command = new DeleteStudentCommand(UUID.randomUUID());
    Set<ConstraintViolation<DeleteStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(0, violations.size());
  }

  @Test
  void nullStudentId_fails() {
    DeleteStudentCommand command = new DeleteStudentCommand(null);
    Set<ConstraintViolation<DeleteStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(1, violations.size());
    ConstraintViolation<DeleteStudentCommand> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("studentId", violation.getPropertyPath().toString());
  }
}

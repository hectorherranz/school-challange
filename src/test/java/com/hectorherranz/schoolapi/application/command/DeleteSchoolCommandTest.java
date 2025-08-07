package com.hectorherranz.schoolapi.application.command;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DeleteSchoolCommandTest {

  private static Validator VALIDATOR;

  @BeforeAll
  static void setUp() {
    VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validCommand_passes() {
    DeleteSchoolCommand command = new DeleteSchoolCommand(UUID.randomUUID());
    Set<ConstraintViolation<DeleteSchoolCommand>> violations = VALIDATOR.validate(command);

    assertEquals(0, violations.size());
  }

  @Test
  void nullSchoolId_fails() {
    DeleteSchoolCommand command = new DeleteSchoolCommand(null);
    Set<ConstraintViolation<DeleteSchoolCommand>> violations = VALIDATOR.validate(command);

    assertEquals(1, violations.size());
    ConstraintViolation<DeleteSchoolCommand> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("schoolId", violation.getPropertyPath().toString());
  }
}

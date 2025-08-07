package com.hectorherranz.schoolapi.application.command;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CreateSchoolCommandTest {

  private static Validator VALIDATOR;

  @BeforeAll
  static void setUp() {
    VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validCommand_passes() {
    CreateSchoolCommand command = new CreateSchoolCommand("Hogwarts", 500);
    Set<ConstraintViolation<CreateSchoolCommand>> violations = VALIDATOR.validate(command);

    assertEquals(0, violations.size());
  }

  @Test
  void blankName_fails() {
    CreateSchoolCommand command = new CreateSchoolCommand(" ", 500);
    Set<ConstraintViolation<CreateSchoolCommand>> violations = VALIDATOR.validate(command);

    assertEquals(1, violations.size());
    ConstraintViolation<CreateSchoolCommand> violation = violations.iterator().next();
    assertEquals("must not be blank", violation.getMessage());
    assertEquals("name", violation.getPropertyPath().toString());
  }

  @Test
  void negativeCapacity_fails() {
    CreateSchoolCommand command = new CreateSchoolCommand("Hogwarts", -1);
    Set<ConstraintViolation<CreateSchoolCommand>> violations = VALIDATOR.validate(command);

    assertTrue(violations.size() >= 1);
    boolean hasCapacityViolation =
        violations.stream()
            .anyMatch(violation -> "capacity".equals(violation.getPropertyPath().toString()));
    assertTrue(hasCapacityViolation);
  }
}

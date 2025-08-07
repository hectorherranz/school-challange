package com.hectorherranz.schoolapi.application.command;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UpdateStudentCommandTest {

  private static Validator VALIDATOR;
  private static final UUID VALID_STUDENT_ID = UUID.randomUUID();

  @BeforeAll
  static void setUp() {
    VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validCommand_passes() {
    UpdateStudentCommand command = new UpdateStudentCommand(VALID_STUDENT_ID, "Hermione Granger");
    Set<ConstraintViolation<UpdateStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(0, violations.size());
  }

  @Test
  void nullStudentId_fails() {
    UpdateStudentCommand command = new UpdateStudentCommand(null, "Hermione Granger");
    Set<ConstraintViolation<UpdateStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(1, violations.size());
    ConstraintViolation<UpdateStudentCommand> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("studentId", violation.getPropertyPath().toString());
  }

  @Test
  void blankName_fails() {
    UpdateStudentCommand command = new UpdateStudentCommand(VALID_STUDENT_ID, " ");
    Set<ConstraintViolation<UpdateStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(1, violations.size());
    ConstraintViolation<UpdateStudentCommand> violation = violations.iterator().next();
    assertEquals("must not be blank", violation.getMessage());
    assertEquals("name", violation.getPropertyPath().toString());
  }

  @Test
  void bothInvalidFields_fails() {
    UpdateStudentCommand command = new UpdateStudentCommand(null, " ");
    Set<ConstraintViolation<UpdateStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(2, violations.size());
    boolean hasStudentIdViolation =
        violations.stream()
            .anyMatch(violation -> "studentId".equals(violation.getPropertyPath().toString()));
    boolean hasNameViolation =
        violations.stream()
            .anyMatch(violation -> "name".equals(violation.getPropertyPath().toString()));

    assertTrue(hasStudentIdViolation);
    assertTrue(hasNameViolation);
  }
}

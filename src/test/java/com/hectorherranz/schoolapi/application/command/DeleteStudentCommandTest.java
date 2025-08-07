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
  private static final UUID VALID_SCHOOL_ID = UUID.randomUUID();
  private static final UUID VALID_STUDENT_ID = UUID.randomUUID();

  @BeforeAll
  static void setUp() {
    VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validCommand_passes() {
    DeleteStudentCommand command = new DeleteStudentCommand(VALID_SCHOOL_ID, VALID_STUDENT_ID);
    Set<ConstraintViolation<DeleteStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(0, violations.size());
  }

  @Test
  void nullSchoolId_fails() {
    DeleteStudentCommand command = new DeleteStudentCommand(null, VALID_STUDENT_ID);
    Set<ConstraintViolation<DeleteStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(1, violations.size());
    ConstraintViolation<DeleteStudentCommand> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("schoolId", violation.getPropertyPath().toString());
  }

  @Test
  void nullStudentId_fails() {
    DeleteStudentCommand command = new DeleteStudentCommand(VALID_SCHOOL_ID, null);
    Set<ConstraintViolation<DeleteStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(1, violations.size());
    ConstraintViolation<DeleteStudentCommand> violation = violations.iterator().next();
    assertEquals("must not be null", violation.getMessage());
    assertEquals("studentId", violation.getPropertyPath().toString());
  }

  @Test
  void bothNullIds_fails() {
    DeleteStudentCommand command = new DeleteStudentCommand(null, null);
    Set<ConstraintViolation<DeleteStudentCommand>> violations = VALIDATOR.validate(command);

    assertEquals(2, violations.size());
    boolean hasSchoolIdViolation =
        violations.stream()
            .anyMatch(violation -> "schoolId".equals(violation.getPropertyPath().toString()));
    boolean hasStudentIdViolation =
        violations.stream()
            .anyMatch(violation -> "studentId".equals(violation.getPropertyPath().toString()));

    assertTrue(hasSchoolIdViolation);
    assertTrue(hasStudentIdViolation);
  }
}

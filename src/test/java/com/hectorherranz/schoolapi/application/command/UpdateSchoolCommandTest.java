package com.hectorherranz.schoolapi.application.command;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class UpdateSchoolCommandTest {

  private static Validator validator;

  private static final UUID VALID_SCHOOL_ID = UUID.randomUUID();

  @BeforeAll
  static void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validCommand_passes() {
    UpdateSchoolCommand cmd =
        new UpdateSchoolCommand(VALID_SCHOOL_ID, Optional.of("Hogwarts"), Optional.of(500));
    assertTrue(validator.validate(cmd).isEmpty());
  }

  @Test
  void nullSchoolId_fails() {
    UpdateSchoolCommand cmd =
        new UpdateSchoolCommand(null, Optional.of("Hogwarts"), Optional.of(500));
    assertSingleViolation(cmd, "schoolId", "must not be null");
  }

  @Test
  void emptyOptionals_passes() {
    UpdateSchoolCommand cmd =
        new UpdateSchoolCommand(VALID_SCHOOL_ID, Optional.empty(), Optional.empty());
    assertTrue(validator.validate(cmd).isEmpty());
  }

  @Test
  void blankNameInOptional_passes() {
    UpdateSchoolCommand cmd =
        new UpdateSchoolCommand(VALID_SCHOOL_ID, Optional.of(" "), Optional.of(500));
    assertTrue(validator.validate(cmd).isEmpty());
  }

  @Test
  void negativeCapacityInOptional_passes() {
    UpdateSchoolCommand cmd =
        new UpdateSchoolCommand(VALID_SCHOOL_ID, Optional.of("Hogwarts"), Optional.of(-1));
    assertTrue(validator.validate(cmd).isEmpty());
  }

  /* helper */
  private void assertSingleViolation(Object obj, String path, String message) {
    Set<ConstraintViolation<Object>> v = validator.validate(obj);
    assertEquals(1, v.size());
    ConstraintViolation<?> cv = v.iterator().next();
    assertEquals(path, cv.getPropertyPath().toString());
    assertEquals(message, cv.getMessage());
  }
}

package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SchoolRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validRequest_passes() {
    // Arrange
    SchoolRequest request = new SchoolRequest("Hogwarts School", 500);

    // Act
    Set<ConstraintViolation<SchoolRequest>> violations = validator.validate(request);

    // Assert
    assertTrue(violations.isEmpty());
  }

  @Test
  void blankName_fails() {
    // Arrange
    SchoolRequest request = new SchoolRequest("   ", 500);

    // Act
    Set<ConstraintViolation<SchoolRequest>> violations = validator.validate(request);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<SchoolRequest> violation = violations.iterator().next();
    assertEquals("name", violation.getPropertyPath().toString());
    assertEquals("School name is required", violation.getMessage());
  }

  @Test
  void nullName_fails() {
    // Arrange
    SchoolRequest request = new SchoolRequest(null, 500);

    // Act
    Set<ConstraintViolation<SchoolRequest>> violations = validator.validate(request);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<SchoolRequest> violation = violations.iterator().next();
    assertEquals("name", violation.getPropertyPath().toString());
    assertEquals("School name is required", violation.getMessage());
  }

  @Test
  void negativeCapacity_fails() {
    // Arrange
    SchoolRequest request = new SchoolRequest("Hogwarts School", -1);

    // Act
    Set<ConstraintViolation<SchoolRequest>> violations = validator.validate(request);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<SchoolRequest> violation = violations.iterator().next();
    assertEquals("capacity", violation.getPropertyPath().toString());
    assertEquals("School capacity must be at least 50", violation.getMessage());
  }

  @Test
  void zeroCapacity_fails() {
    // Arrange
    SchoolRequest request = new SchoolRequest("Hogwarts School", 0);

    // Act
    Set<ConstraintViolation<SchoolRequest>> violations = validator.validate(request);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<SchoolRequest> violation = violations.iterator().next();
    assertEquals("capacity", violation.getPropertyPath().toString());
    assertEquals("School capacity must be at least 50", violation.getMessage());
  }

  @Test
  void multipleViolations_fails() {
    // Arrange
    SchoolRequest request = new SchoolRequest("", -5);

    // Act
    Set<ConstraintViolation<SchoolRequest>> violations = validator.validate(request);

    // Assert
    assertEquals(2, violations.size());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("capacity")));
  }
}

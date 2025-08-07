package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StudentRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void validRequest_passes() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("Harry Potter", schoolId);

    // Act
    Set<ConstraintViolation<StudentRequest>> violations = validator.validate(request);

    // Assert
    assertTrue(violations.isEmpty());
  }

  @Test
  void blankName_fails() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest("   ", schoolId);

    // Act
    Set<ConstraintViolation<StudentRequest>> violations = validator.validate(request);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<StudentRequest> violation = violations.iterator().next();
    assertEquals("name", violation.getPropertyPath().toString());
    assertEquals("Student name is required", violation.getMessage());
  }

  @Test
  void nullName_fails() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    StudentRequest request = new StudentRequest(null, schoolId);

    // Act
    Set<ConstraintViolation<StudentRequest>> violations = validator.validate(request);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<StudentRequest> violation = violations.iterator().next();
    assertEquals("name", violation.getPropertyPath().toString());
    assertEquals("Student name is required", violation.getMessage());
  }

  @Test
  void nullSchoolId_fails() {
    // Arrange
    StudentRequest request = new StudentRequest("Harry Potter", null);

    // Act
    Set<ConstraintViolation<StudentRequest>> violations = validator.validate(request);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<StudentRequest> violation = violations.iterator().next();
    assertEquals("schoolId", violation.getPropertyPath().toString());
    assertEquals("School ID is required", violation.getMessage());
  }

  @Test
  void multipleViolations_fails() {
    // Arrange
    StudentRequest request = new StudentRequest("", null);

    // Act
    Set<ConstraintViolation<StudentRequest>> violations = validator.validate(request);

    // Assert
    assertEquals(2, violations.size());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    assertTrue(
        violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("schoolId")));
  }
}

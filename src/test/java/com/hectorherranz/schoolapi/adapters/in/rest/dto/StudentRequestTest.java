package com.hectorherranz.schoolapi.adapters.in.rest.dto;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StudentRequestTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldCreateValidStudentRequest() {
    // Given
    String name = "Harry Potter";

    // When
    StudentRequest request = new StudentRequest(name);

    // Then
    assertEquals(name, request.name());
  }

  @Test
  void shouldFailValidationWithBlankName() {
    // Given
    StudentRequest request = new StudentRequest("   ");

    // When
    Set<ConstraintViolation<StudentRequest>> violations = validator.validate(request);

    // Then
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  void shouldFailValidationWithNullName() {
    // Given
    StudentRequest request = new StudentRequest(null);

    // When
    Set<ConstraintViolation<StudentRequest>> violations = validator.validate(request);

    // Then
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }

  @Test
  void shouldPassValidationWithValidName() {
    // Given
    StudentRequest request = new StudentRequest("Harry Potter");

    // When
    Set<ConstraintViolation<StudentRequest>> violations = validator.validate(request);

    // Then
    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldPassValidationWithEmptyName() {
    // Given
    StudentRequest request = new StudentRequest("");

    // When
    Set<ConstraintViolation<StudentRequest>> violations = validator.validate(request);

    // Then
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
  }
}

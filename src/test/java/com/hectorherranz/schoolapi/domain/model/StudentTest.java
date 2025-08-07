package com.hectorherranz.schoolapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StudentTest {

  private UUID studentId;
  private String studentName;
  private UUID schoolId;
  private Student student;

  @BeforeEach
  void setUp() {
    studentId = UUID.randomUUID();
    studentName = "John Doe";
    schoolId = UUID.randomUUID();
    student = new Student(studentId, studentName, schoolId);
  }

  @Test
  void shouldCreateStudentWithValidData() {
    // When & Then
    assertEquals(studentId, student.id());
    assertEquals(studentName, student.name());
    assertEquals(schoolId, student.schoolId());
  }

  @Test
  void shouldTrimStudentNameInConstructor() {
    // Given
    String nameWithSpaces = "  John Doe  ";

    // When
    Student studentWithSpaces = new Student(studentId, nameWithSpaces, schoolId);

    // Then
    assertEquals("John Doe", studentWithSpaces.name());
  }

  @Test
  void shouldHandleEmptyName() {
    // Given
    String emptyName = "   ";

    // When
    Student studentWithEmptyName = new Student(studentId, emptyName, schoolId);

    // Then
    assertEquals("", studentWithEmptyName.name());
  }

  @Test
  void shouldHandleNullName() {
    // Given
    String nullName = null;

    // When & Then
    assertThrows(NullPointerException.class, () -> new Student(studentId, nullName, schoolId));
  }

  @Test
  void shouldHandleNullSchoolId() {
    // Given
    UUID nullSchoolId = null;

    // When & Then
    assertThrows(
        NullPointerException.class, () -> new Student(studentId, studentName, nullSchoolId));
  }

  @Test
  void shouldHandleNullStudentId() {
    // Given
    UUID nullStudentId = null;

    // When & Then
    assertThrows(
        NullPointerException.class, () -> new Student(nullStudentId, studentName, schoolId));
  }
}

package com.hectorherranz.schoolapi.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.hectorherranz.schoolapi.domain.exception.CapacityExceededException;
import com.hectorherranz.schoolapi.domain.model.draft.StudentDraft;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchoolTest {

  private UUID schoolId;
  private String schoolName;
  private Capacity capacity;
  private School school;

  @BeforeEach
  void setUp() {
    schoolId = UUID.randomUUID();
    schoolName = "Test School";
    capacity = new Capacity(100);
    school = new School(schoolId, schoolName, capacity);
  }

  @Test
  void shouldCreateSchoolWithValidData() {
    // When & Then
    assertEquals(schoolId, school.id());
    assertEquals(schoolName, school.name());
    assertEquals(capacity, school.capacity());
    assertEquals(0, school.enrolledCount());
    assertTrue(school.students().isEmpty());
  }

  @Test
  void shouldEnrollStudentWhenCapacityAvailable() {
    // Given
    StudentDraft draft = new StudentDraft("John Doe");

    // When
    Student student = school.enrollStudent(draft);

    // Then
    assertNotNull(student);
    assertEquals("John Doe", student.name());
    assertEquals(schoolId, student.schoolId());
    assertEquals(1, school.enrolledCount());
    assertEquals(1, school.students().size());
    assertEquals(student, school.students().get(0));
  }

  @Test
  void shouldThrowExceptionWhenEnrollingStudentBeyondCapacity() {
    // Given
    Capacity smallCapacity = new Capacity(50);
    School smallSchool = new School(UUID.randomUUID(), "Small School", smallCapacity);

    // Enroll 50 students (at capacity)
    for (int i = 1; i <= 50; i++) {
      smallSchool.enrollStudent(new StudentDraft("Student " + i));
    }

    // Try to enroll one more student
    StudentDraft extraStudent = new StudentDraft("Extra Student");

    // When & Then
    assertThrows(CapacityExceededException.class, () -> smallSchool.enrollStudent(extraStudent));
  }

  @Test
  void shouldChangeSchoolName() {
    // Given
    String newName = "Updated School Name";

    // When
    school.changeName(newName);

    // Then
    assertEquals(newName, school.name());
  }

  @Test
  void shouldTrimSchoolNameWhenChanged() {
    // Given
    String newNameWithSpaces = "  Updated School Name  ";

    // When
    school.changeName(newNameWithSpaces);

    // Then
    assertEquals("Updated School Name", school.name());
  }

  @Test
  void shouldResizeCapacityWhenNewCapacityIsSufficient() {
    // Given
    Capacity newCapacity = new Capacity(200);

    // When
    school.resizeCapacity(newCapacity);

    // Then
    assertEquals(newCapacity, school.capacity());
  }

  @Test
  void shouldThrowExceptionWhenResizingToInsufficientCapacity() {
    // Given
    // Enroll 51 students (more than minimum capacity of 50)
    for (int i = 1; i <= 51; i++) {
      school.enrollStudent(new StudentDraft("Student " + i));
    }
    Capacity insufficientCapacity =
        new Capacity(50); // Valid capacity but insufficient for 51 students

    // When & Then
    assertThrows(IllegalArgumentException.class, () -> school.resizeCapacity(insufficientCapacity));
  }

  @Test
  void shouldReturnUnmodifiableStudentsList() {
    // Given
    school.enrollStudent(new StudentDraft("Student 1"));

    // When & Then
    assertThrows(UnsupportedOperationException.class, () -> school.students().add(null));
  }

  @Test
  void shouldTrimNameInConstructor() {
    // Given
    String nameWithSpaces = "  Test School  ";

    // When
    School schoolWithSpaces = new School(schoolId, nameWithSpaces, capacity);

    // Then
    assertEquals("Test School", schoolWithSpaces.name());
  }
}

package com.hectorherranz.schoolapi.adapters.in.rest.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolDetail;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolRequest;
import com.hectorherranz.schoolapi.adapters.in.rest.dto.SchoolSummary;
import com.hectorherranz.schoolapi.application.command.CreateSchoolCommand;
import com.hectorherranz.schoolapi.application.command.UpdateSchoolCommand;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchoolDtoMapperTest {

  private SchoolDtoMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new SchoolDtoMapper();
  }

  @Test
  void toCreateCommand_ValidRequest_ReturnsCorrectCommand() {
    // Arrange
    String name = "Hogwarts School";
    int capacity = 500;
    SchoolRequest request = new SchoolRequest(name, capacity);

    // Act
    CreateSchoolCommand command = mapper.toCreateCommand(request);

    // Assert
    assertEquals(name, command.name());
    assertEquals(capacity, command.capacity());
  }

  @Test
  void toUpdateCommand_ValidRequest_ReturnsCorrectCommand() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String name = "Updated School Name";
    int capacity = 600;
    SchoolRequest request = new SchoolRequest(name, capacity);

    // Act
    UpdateSchoolCommand command = mapper.toUpdateCommand(schoolId, request);

    // Assert
    assertEquals(schoolId, command.schoolId());
    assertEquals(Optional.of(name), command.name());
    assertEquals(Optional.of(capacity), command.capacity());
  }

  @Test
  void toDetail_ValidSchool_ReturnsCorrectDetail() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String name = "Hogwarts School";
    int capacity = 500;
    School school = new School(schoolId, name, new Capacity(capacity));

    // Act
    SchoolDetail detail = mapper.toDetail(school);

    // Assert
    assertEquals(schoolId, detail.id());
    assertEquals(name, detail.name());
    assertEquals(capacity, detail.capacity());
    assertEquals(0, detail.enrolledStudents()); // No students enrolled initially
  }

  @Test
  void toDetail_SchoolWithStudents_ReturnsCorrectEnrolledCount() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String name = "Hogwarts School";
    int capacity = 500;
    School school =
        School.rehydrate(
            schoolId,
            name,
            new Capacity(capacity),
            List.of(
                new Student(UUID.randomUUID(), "Harry Potter", schoolId),
                new Student(UUID.randomUUID(), "Hermione Granger", schoolId)));

    // Act
    SchoolDetail detail = mapper.toDetail(school);

    // Assert
    assertEquals(schoolId, detail.id());
    assertEquals(name, detail.name());
    assertEquals(capacity, detail.capacity());
    assertEquals(2, detail.enrolledStudents());
  }

  @Test
  void toSummary_ValidSchool_ReturnsCorrectSummary() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String name = "Hogwarts School";
    int capacity = 500;
    School school = new School(schoolId, name, new Capacity(capacity));

    // Act
    SchoolSummary summary = mapper.toSummary(school);

    // Assert
    assertEquals(schoolId, summary.id());
    assertEquals(name, summary.name());
    assertEquals(capacity, summary.capacity());
    assertEquals(0, summary.enrolledStudents());
  }

  @Test
  void toSummary_SchoolWithStudents_ReturnsCorrectEnrolledCount() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String name = "Hogwarts School";
    int capacity = 500;
    School school =
        School.rehydrate(
            schoolId,
            name,
            new Capacity(capacity),
            List.of(
                new Student(UUID.randomUUID(), "Harry Potter", schoolId),
                new Student(UUID.randomUUID(), "Hermione Granger", schoolId),
                new Student(UUID.randomUUID(), "Ron Weasley", schoolId)));

    // Act
    SchoolSummary summary = mapper.toSummary(school);

    // Assert
    assertEquals(schoolId, summary.id());
    assertEquals(name, summary.name());
    assertEquals(capacity, summary.capacity());
    assertEquals(3, summary.enrolledStudents());
  }
}

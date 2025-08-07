package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.entity.StudentEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.mapper.StudentEntityMapper;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class StudentJpaRepositoryTest {

  @Autowired private SpringDataStudentRepository springDataRepository;

  @Autowired private SpringDataSchoolRepository schoolRepository;

  private StudentRepositoryPort studentRepository;

  @BeforeEach
  void setUp() {
    studentRepository = new StudentJpaRepository(springDataRepository);
  }

  @Test
  void shouldUpdateAndFindStudentById() {
    // Given
    // Create and save school first
    SchoolEntity school = new SchoolEntity("Test School", new Capacity(100));
    school.setId(UUID.randomUUID());
    SchoolEntity savedSchool = schoolRepository.save(school);

    // Create student via School aggregate (this would normally happen through SchoolJpaRepository)
    StudentEntity studentEntity = new StudentEntity(UUID.randomUUID(), "John Doe", savedSchool);
    StudentEntity savedStudentEntity = springDataRepository.save(studentEntity);
    Student student = StudentEntityMapper.toDomain(savedStudentEntity);

    // When - update the student
    Student updatedStudent = new Student(student.id(), "Updated John Doe", student.schoolId());
    Student saved = studentRepository.save(updatedStudent);
    Optional<Student> found = studentRepository.findById(saved.id());

    // Then
    assertTrue(found.isPresent());
    assertEquals(saved.id(), found.get().id());
    assertEquals("Updated John Doe", found.get().name());
    assertEquals(savedSchool.getId(), found.get().schoolId());
  }

  @Test
  void shouldReturnEmptyWhenStudentNotFound() {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    // When
    Optional<Student> found = studentRepository.findById(nonExistentId);

    // Then
    assertFalse(found.isPresent());
  }

  @Test
  void shouldSearchStudentsBySchoolIdAndName() {
    // Given
    // Create schools first
    SchoolEntity school1 = new SchoolEntity("School 1", new Capacity(100));
    school1.setId(UUID.randomUUID());
    SchoolEntity savedSchool1 = schoolRepository.save(school1);

    SchoolEntity school2 = new SchoolEntity("School 2", new Capacity(100));
    school2.setId(UUID.randomUUID());
    SchoolEntity savedSchool2 = schoolRepository.save(school2);

    // Create students via direct entity creation (simulating School aggregate creation)
    StudentEntity studentEntity1 = new StudentEntity(UUID.randomUUID(), "John Smith", savedSchool1);
    StudentEntity studentEntity2 = new StudentEntity(UUID.randomUUID(), "Jane Smith", savedSchool1);
    StudentEntity studentEntity3 =
        new StudentEntity(UUID.randomUUID(), "Bob Johnson", savedSchool2);

    springDataRepository.save(studentEntity1);
    springDataRepository.save(studentEntity2);
    springDataRepository.save(studentEntity3);

    // When
    Page<Student> results =
        studentRepository.search(savedSchool1.getId(), "smith", PageRequest.of(0, 10));

    // Then
    assertEquals(2, results.getTotalElements());
    assertTrue(
        results.getContent().stream()
            .allMatch(student -> student.name().toLowerCase().contains("smith")));
    assertTrue(
        results.getContent().stream()
            .allMatch(student -> student.schoolId().equals(savedSchool1.getId())));
  }

  @Test
  void shouldSearchStudentsCaseInsensitive() {
    // Given
    SchoolEntity school = new SchoolEntity("Test School", new Capacity(100));
    school.setId(UUID.randomUUID());
    SchoolEntity savedSchool = schoolRepository.save(school);

    // Create student via direct entity creation (simulating School aggregate creation)
    StudentEntity studentEntity = new StudentEntity(UUID.randomUUID(), "John Smith", savedSchool);
    springDataRepository.save(studentEntity);

    // When
    Page<Student> results1 =
        studentRepository.search(savedSchool.getId(), "JOHN", PageRequest.of(0, 10));
    Page<Student> results2 =
        studentRepository.search(savedSchool.getId(), "smith", PageRequest.of(0, 10));

    // Then
    assertEquals(1, results1.getTotalElements());
    assertEquals(1, results2.getTotalElements());
  }

  @Test
  void shouldDeleteStudentById() {
    // Given
    // Create and save school first
    SchoolEntity school = new SchoolEntity("Test School", new Capacity(100));
    school.setId(UUID.randomUUID());
    SchoolEntity savedSchool = schoolRepository.save(school);

    // Create student via direct entity creation (simulating School aggregate creation)
    StudentEntity studentEntity =
        new StudentEntity(UUID.randomUUID(), "Student to Delete", savedSchool);
    StudentEntity savedStudentEntity = springDataRepository.save(studentEntity);
    Student student = StudentEntityMapper.toDomain(savedStudentEntity);

    // Verify student exists
    assertTrue(studentRepository.findById(student.id()).isPresent());

    // When
    studentRepository.deleteById(student.id());

    // Then
    assertFalse(studentRepository.findById(student.id()).isPresent());
  }

  @Test
  void shouldUpdateExistingStudent() {
    // Given
    // Create and save school first
    SchoolEntity school = new SchoolEntity("Test School", new Capacity(100));
    school.setId(UUID.randomUUID());
    SchoolEntity savedSchool = schoolRepository.save(school);

    // Create student via direct entity creation (simulating School aggregate creation)
    StudentEntity studentEntity =
        new StudentEntity(UUID.randomUUID(), "Original Name", savedSchool);
    StudentEntity savedStudentEntity = springDataRepository.save(studentEntity);
    Student originalStudent = StudentEntityMapper.toDomain(savedStudentEntity);

    // When
    Student updatedStudent =
        new Student(originalStudent.id(), "Updated Name", originalStudent.schoolId());
    Student saved = studentRepository.save(updatedStudent);

    // Then
    assertEquals("Updated Name", saved.name());

    Optional<Student> found = studentRepository.findById(originalStudent.id());
    assertTrue(found.isPresent());
    assertEquals("Updated Name", found.get().name());
  }

  @Test
  void shouldHandlePaginationInSearch() {
    // Given
    SchoolEntity school = new SchoolEntity("Test School", new Capacity(100));
    school.setId(UUID.randomUUID());
    SchoolEntity savedSchool = schoolRepository.save(school);

    // Create students via direct entity creation (simulating School aggregate creation)
    for (int i = 1; i <= 25; i++) {
      StudentEntity studentEntity =
          new StudentEntity(UUID.randomUUID(), "Student " + i, savedSchool);
      springDataRepository.save(studentEntity);
    }

    // When
    Page<Student> firstPage =
        studentRepository.search(savedSchool.getId(), "Student", PageRequest.of(0, 10));
    Page<Student> secondPage =
        studentRepository.search(savedSchool.getId(), "Student", PageRequest.of(1, 10));

    // Then
    assertEquals(25, firstPage.getTotalElements());
    assertEquals(10, firstPage.getContent().size());
    assertEquals(10, secondPage.getContent().size());
    assertNotEquals(firstPage.getContent().get(0).id(), secondPage.getContent().get(0).id());
  }

  @Test
  void shouldReturnEmptyPageWhenNoStudentsInSchool() {
    // Given
    SchoolEntity school = new SchoolEntity("Test School", new Capacity(100));
    school.setId(UUID.randomUUID());
    SchoolEntity savedSchool = schoolRepository.save(school);

    // When
    Page<Student> results =
        studentRepository.search(savedSchool.getId(), "any", PageRequest.of(0, 10));

    // Then
    assertEquals(0, results.getTotalElements());
    assertTrue(results.getContent().isEmpty());
  }
}

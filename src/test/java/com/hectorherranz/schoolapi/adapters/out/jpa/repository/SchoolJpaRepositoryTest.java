package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
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
class SchoolJpaRepositoryTest {

  @Autowired private SpringDataSchoolRepository springDataRepository;

  private SchoolRepositoryPort schoolRepository;

  @BeforeEach
  void setUp() {
    schoolRepository = new SchoolJpaRepository(springDataRepository);
  }

  @Test
  void shouldSaveAndFindSchoolById() {
    // Given
    School school = new School(UUID.randomUUID(), "Test School", new Capacity(100));

    // When
    School saved = schoolRepository.save(school);
    Optional<School> found = schoolRepository.findById(saved.id());

    // Then
    assertTrue(found.isPresent());
    assertEquals(saved.id(), found.get().id());
    assertEquals("Test School", found.get().name());
    assertEquals(100, found.get().capacity().value());
  }

  @Test
  void shouldReturnEmptyWhenSchoolNotFound() {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    // When
    Optional<School> found = schoolRepository.findById(nonExistentId);

    // Then
    assertFalse(found.isPresent());
  }

  @Test
  void shouldCheckIfSchoolExistsByNameIgnoreCase() {
    // Given
    School school = new School(UUID.randomUUID(), "Unique School", new Capacity(100));
    School savedSchool = schoolRepository.save(school);

    // When & Then
    assertTrue(schoolRepository.existsByNameIgnoreCase("unique school"));
    assertTrue(schoolRepository.existsByNameIgnoreCase("UNIQUE SCHOOL"));
    assertTrue(schoolRepository.existsByNameIgnoreCase("Unique School"));
    assertFalse(schoolRepository.existsByNameIgnoreCase("Non Existent School"));
  }

  @Test
  void shouldSearchSchoolsByName() {
    // Given
    School school1 = new School(UUID.randomUUID(), "Springfield High", new Capacity(100));
    School school2 = new School(UUID.randomUUID(), "Springfield Elementary", new Capacity(50));
    School school3 = new School(UUID.randomUUID(), "Lincoln Academy", new Capacity(200));

    School savedSchool1 = schoolRepository.save(school1);
    School savedSchool2 = schoolRepository.save(school2);
    School savedSchool3 = schoolRepository.save(school3);

    // When
    Page<School> results = schoolRepository.searchByName("springfield", PageRequest.of(0, 10));

    // Then
    assertEquals(2, results.getTotalElements());
    assertTrue(
        results.getContent().stream()
            .allMatch(school -> school.name().toLowerCase().contains("springfield")));
  }

  @Test
  void shouldDeleteSchoolById() {
    // Given
    School school = new School(UUID.randomUUID(), "School to Delete", new Capacity(100));
    School savedSchool = schoolRepository.save(school);

    // Verify school exists
    assertTrue(schoolRepository.findById(savedSchool.id()).isPresent());

    // When
    schoolRepository.deleteById(savedSchool.id());

    // Then
    assertFalse(schoolRepository.findById(savedSchool.id()).isPresent());
  }

  @Test
  void shouldPreserveDomainGeneratedId() {
    // Given
    UUID customId = UUID.randomUUID();
    School school = new School(customId, "Test School", new Capacity(100));

    // When
    School saved = schoolRepository.save(school);

    // Then
    assertEquals(customId, saved.id()); // Domain-generated UUID is preserved
    assertEquals("Test School", saved.name());
    assertEquals(100, saved.capacity().value());
  }

  @Test
  void shouldHandlePaginationInSearch() {
    // Given
    for (int i = 1; i <= 25; i++) {
      School school = new School(UUID.randomUUID(), "School " + i, new Capacity(100));
      schoolRepository.save(school);
    }

    // When
    Page<School> firstPage = schoolRepository.searchByName("School", PageRequest.of(0, 10));
    Page<School> secondPage = schoolRepository.searchByName("School", PageRequest.of(1, 10));

    // Then
    assertEquals(25, firstPage.getTotalElements());
    assertEquals(10, firstPage.getContent().size());
    assertEquals(10, secondPage.getContent().size());
    assertNotEquals(firstPage.getContent().get(0).id(), secondPage.getContent().get(0).id());
  }
}

package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class SchoolOptimisticLockingTest {

  @Autowired private SpringDataSchoolRepository schoolRepository;

  private UUID schoolId;

  @BeforeEach
  @Transactional
  void setUp() {
    // Create a test school
    SchoolEntity school = new SchoolEntity("Test School", new Capacity(100));
    school.setId(UUID.randomUUID());
    SchoolEntity savedSchool = schoolRepository.save(school);
    schoolId = savedSchool.getId();
  }

  @Test
  void shouldFailWithOptimisticLockingExceptionOnStaleVersion() {
    // Load the school
    SchoolEntity school = schoolRepository.findById(schoolId).orElseThrow();

    // Update the school to increment its version
    school.setName("First Update");
    SchoolEntity updatedSchool = schoolRepository.save(school);
    Long currentVersion = updatedSchool.getVersion();

    // Create a new entity with the same ID but stale version
    SchoolEntity staleSchool = new SchoolEntity("Stale Update", new Capacity(100));
    staleSchool.setId(schoolId);
    staleSchool.setVersion(currentVersion - 1); // Use stale version

    // This should fail with OptimisticLockingFailureException
    assertThatThrownBy(() -> schoolRepository.save(staleSchool))
        .isInstanceOf(OptimisticLockingFailureException.class);
  }

  @Test
  void shouldIncrementVersionOnEachUpdate() {
    // Load the school
    SchoolEntity school = schoolRepository.findById(schoolId).orElseThrow();
    Long version1 = school.getVersion();

    // First update
    school.setName("First Update");
    SchoolEntity saved1 = schoolRepository.save(school);
    Long version2 = saved1.getVersion();

    // Second update
    saved1.setName("Second Update");
    SchoolEntity saved2 = schoolRepository.save(saved1);
    Long version3 = saved2.getVersion();

    // Verify versions are incrementing
    assertThat(version2).isEqualTo(version1 + 1);
    assertThat(version3).isEqualTo(version2 + 1);
    assertThat(version3).isEqualTo(version1 + 2);
  }
}

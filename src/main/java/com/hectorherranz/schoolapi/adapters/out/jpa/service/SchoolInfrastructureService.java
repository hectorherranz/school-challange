package com.hectorherranz.schoolapi.adapters.out.jpa.service;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.repository.SpringDataSchoolRepository;
import com.hectorherranz.schoolapi.domain.exception.DuplicateNameException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service intentionally works with JPA entities to leverage Hibernate session optimizations;
 * domain ports remain entity-agnostic.
 *
 * <p>This service leverages Hibernate optimizations for complex operations by working directly with
 * entities to maximize performance. It serves as a bridge between domain logic and infrastructure
 * optimizations, ensuring that entity-level access stays contained within the infrastructure layer
 * while maintaining clean domain boundaries.
 */
@Service
@Transactional
public class SchoolInfrastructureService {

  private final SpringDataSchoolRepository schoolRepository;

  public SchoolInfrastructureService(SpringDataSchoolRepository schoolRepository) {
    this.schoolRepository = schoolRepository;
  }

  /**
   * Optimized update operation that leverages Hibernate session. Works directly with entities to
   * maximize performance.
   */
  public void updateSchoolOptimized(
      UUID schoolId, Optional<String> newName, Optional<Integer> newCapacity) {
    // Load entity directly with pessimistic lock (stays in Hibernate session)
    SchoolEntity school =
        schoolRepository
            .findByIdBasicForUpdate(schoolId)
            .orElseThrow(() -> new NotFoundException("School", schoolId.toString()));

    // Update name if provided
    newName.ifPresent(
        name -> {
          // Check for duplicate name (excluding current school)
          if (!name.equalsIgnoreCase(school.getName())
              && schoolRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateNameException();
          }
          school.setName(name);
        });

    // Update capacity if provided
    newCapacity.ifPresent(
        capacity -> {
          // Use count query to get actual enrollment instead of relying on loaded students
          int currentEnrollment = schoolRepository.countStudentsBySchoolId(schoolId);
          Capacity newCapacityValue = new Capacity(capacity);

          // Validate capacity against actual enrollment
          if (newCapacityValue.value() < currentEnrollment) {
            throw new IllegalArgumentException(
                "New capacity (%d) cannot be less than current enrollment (%d)"
                    .formatted(newCapacityValue.value(), currentEnrollment));
          }

          school.setCapacity(newCapacityValue);
        });

    // Hibernate automatically detects changes and persists them
    // No need to explicitly save - transaction commit will handle it
  }
}

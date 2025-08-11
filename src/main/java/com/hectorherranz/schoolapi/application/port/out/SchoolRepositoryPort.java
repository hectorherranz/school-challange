package com.hectorherranz.schoolapi.application.port.out;

import com.hectorherranz.schoolapi.domain.model.School;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SchoolRepositoryPort {
  Optional<School> findById(UUID id);

  boolean existsByNameIgnoreCase(String name);

  Page<School> searchByName(String name, Pageable pageable);

  School save(School school);

  void deleteById(UUID id);

  // Load school with minimal data for student operations
  Optional<School> findByIdForStudentUpdate(UUID id);

  // Optimized methods for capacity validation
  int countStudentsBySchoolId(UUID schoolId);

  // Load school basic data (without students) for validation
  Optional<School> findByIdBasic(UUID id);

  // New method for selective loading (basic school data only)
  Optional<School> findByIdBasicForUpdate(UUID id);
}

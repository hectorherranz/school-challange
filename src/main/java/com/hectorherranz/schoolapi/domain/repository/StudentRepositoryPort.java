package com.hectorherranz.schoolapi.domain.repository;

import com.hectorherranz.schoolapi.domain.model.Student;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentRepositoryPort {
  Optional<Student> findById(UUID id);

  Page<Student> search(UUID schoolId, String q, Pageable pageable);

  Student save(Student student); // usually cascaded via School, but handy for updates

  void deleteById(UUID id);

  // Optimized methods for school validation
  Optional<Student> findByIdAndSchoolId(UUID studentId, UUID schoolId);

  boolean existsByIdAndSchoolId(UUID studentId, UUID schoolId);

  // New methods for selective loading
  Optional<Student> findStudentByIdAndSchoolId(UUID studentId, UUID schoolId);

  // Hibernate-optimized methods (return entities for complex operations)
  Optional<Object> findStudentEntityByIdAndSchoolId(UUID studentId, UUID schoolId);
}

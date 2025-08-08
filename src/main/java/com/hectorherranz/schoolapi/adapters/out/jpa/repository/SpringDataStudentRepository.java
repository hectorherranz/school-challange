package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.StudentEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataStudentRepository extends JpaRepository<StudentEntity, UUID> {

  Page<StudentEntity> findBySchoolIdAndNameContainingIgnoreCase(
      UUID schoolId, String name, Pageable pageable);

  // Optimized methods for school validation
  Optional<StudentEntity> findByIdAndSchoolId(UUID studentId, UUID schoolId);

  boolean existsByIdAndSchoolId(UUID studentId, UUID schoolId);

  // New method for selective loading
  @Query("SELECT st FROM StudentEntity st WHERE st.id = :studentId AND st.school.id = :schoolId")
  Optional<StudentEntity> findStudentByIdAndSchoolId(
      @Param("studentId") UUID studentId, @Param("schoolId") UUID schoolId);
}

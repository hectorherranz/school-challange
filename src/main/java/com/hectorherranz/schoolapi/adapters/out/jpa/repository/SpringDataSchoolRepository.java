package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataSchoolRepository extends JpaRepository<SchoolEntity, UUID> {

  boolean existsByNameIgnoreCase(String name);

  Page<SchoolEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

  @Query("SELECT s FROM SchoolEntity s LEFT JOIN FETCH s.studentsById WHERE s.id = :id")
  Optional<SchoolEntity> findByIdWithStudentsOnly(@Param("id") UUID id);

  @Query("SELECT COUNT(s) FROM StudentEntity s WHERE s.school.id = :schoolId")
  int countStudentsBySchoolId(@Param("schoolId") UUID schoolId);

  @Query("SELECT s FROM SchoolEntity s WHERE s.id = :id")
  Optional<SchoolEntity> findByIdBasic(@Param("id") UUID id);

  // New method for selective loading (basic school data only)
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM SchoolEntity s WHERE s.id = :id")
  Optional<SchoolEntity> findByIdBasicForUpdate(@Param("id") UUID id);
}

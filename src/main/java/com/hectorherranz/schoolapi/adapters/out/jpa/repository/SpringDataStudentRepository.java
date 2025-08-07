package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.StudentEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SpringDataStudentRepository extends JpaRepository<StudentEntity, UUID> {

  Page<StudentEntity> findBySchoolIdAndNameContainingIgnoreCase(
      UUID schoolId, String name, Pageable pageable);
}

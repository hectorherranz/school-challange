package com.hectorherranz.schoolapi.adapters.out.jpa.service;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.entity.StudentEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.mapper.StudentEntityMapper;
import com.hectorherranz.schoolapi.adapters.out.jpa.repository.SpringDataSchoolRepository;
import com.hectorherranz.schoolapi.adapters.out.jpa.repository.SpringDataStudentRepository;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.Student;
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
public class StudentInfrastructureService {

  private final SpringDataStudentRepository studentRepository;
  private final SpringDataSchoolRepository schoolRepository;

  public StudentInfrastructureService(
      SpringDataStudentRepository studentRepository, SpringDataSchoolRepository schoolRepository) {
    this.studentRepository = studentRepository;
    this.schoolRepository = schoolRepository;
  }

  /**
   * Optimized update operation that leverages Hibernate session. Works directly with entities to
   * maximize performance.
   */
  public void updateStudentOptimized(UUID studentId, UUID schoolId, String newName) {
    // Load entities directly (stays in Hibernate session)
    StudentEntity student =
        studentRepository
            .findStudentByIdAndSchoolId(studentId, schoolId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Student",
                        String.format("Student %s not found in school %s", studentId, schoolId)));

    SchoolEntity school =
        schoolRepository
            .findByIdBasicForUpdate(schoolId)
            .orElseThrow(() -> new NotFoundException("School", schoolId.toString()));

    // Validate student belongs to school
    if (!student.getSchool().getId().equals(schoolId)) {
      throw new NotFoundException("Student", "Student does not belong to this school");
    }

    // Update directly on entity (Hibernate will handle dirty checking)
    student.setName(newName);

    // Hibernate automatically detects changes and persists them
    // No need to explicitly save - transaction commit will handle it
  }

  /** Optimized delete operation that leverages Hibernate session. */
  public void deleteStudentOptimized(UUID studentId, UUID schoolId) {
    StudentEntity student =
        studentRepository
            .findStudentByIdAndSchoolId(studentId, schoolId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Student",
                        String.format("Student %s not found in school %s", studentId, schoolId)));

    // Validate student belongs to school
    if (!student.getSchool().getId().equals(schoolId)) {
      throw new NotFoundException("Student", "Student does not belong to this school");
    }

    // Remove from school's collection (Hibernate handles cascade)
    student.getSchool().getStudentsById().remove(student.getId());

    // Delete student
    studentRepository.delete(student);
  }

  public Student createStudent(Student student) {
    // Get the school entity to establish the relationship
    SchoolEntity schoolEntity =
        schoolRepository
            .findById(student.schoolId())
            .orElseThrow(() -> new NotFoundException("School", student.schoolId().toString()));

    StudentEntity entity = StudentEntityMapper.toEntity(student, schoolEntity);
    StudentEntity saved = studentRepository.save(entity);
    return StudentEntityMapper.toDomain(saved);
  }
}

package com.hectorherranz.schoolapi.adapters.out.jpa.mapper;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.entity.StudentEntity;
import com.hectorherranz.schoolapi.domain.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StudentEntityMapperTest {

    private UUID studentId;
    private String studentName;
    private UUID schoolId;
    private Student student;
    private StudentEntity studentEntity;
    private SchoolEntity schoolEntity;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        studentName = "John Doe";
        schoolId = UUID.randomUUID();
        student = new Student(studentId, studentName, schoolId);
        
        schoolEntity = new SchoolEntity("Test School", new com.hectorherranz.schoolapi.domain.model.valueobject.Capacity(100));
        schoolEntity.setId(schoolId);
        
        studentEntity = new StudentEntity(studentName, schoolEntity);
        studentEntity.setId(studentId);
    }

    @Test
    void shouldMapDomainToEntity() {
        // When
        StudentEntity result = StudentEntityMapper.toEntity(student);

        // Then
        assertNotNull(result);
        assertNull(result.getId()); // ID will be set by @PrePersist when saved
        assertEquals(studentName, result.getName());
        assertNull(result.getSchool()); // School will be set by parent mapper
    }

    @Test
    void shouldMapEntityToDomain() {
        // When
        Student result = StudentEntityMapper.toDomain(studentEntity);

        // Then
        assertNotNull(result);
        assertEquals(studentId, result.id());
        assertEquals(studentName, result.name());
        assertEquals(schoolId, result.schoolId());
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        StudentEntity nullEntity = null;
        Student nullStudent = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> StudentEntityMapper.toDomain(nullEntity));
        assertThrows(NullPointerException.class, () -> StudentEntityMapper.toEntity(nullStudent));
    }

    @Test
    void shouldNotSetIdWhenMapping() {
        // Given
        UUID customId = UUID.randomUUID();
        student = new Student(customId, studentName, schoolId);

        // When
        StudentEntity result = StudentEntityMapper.toEntity(student);

        // Then
        assertNull(result.getId()); // ID will be set by @PrePersist when saved
    }

    @Test
    void shouldPreserveNameWhenMapping() {
        // Given
        String customName = "Jane Smith";
        student = new Student(studentId, customName, schoolId);

        // When
        StudentEntity result = StudentEntityMapper.toEntity(student);

        // Then
        assertEquals(customName, result.getName());
    }

    @Test
    void shouldExtractSchoolIdFromEntity() {
        // Given
        UUID differentSchoolId = UUID.randomUUID();
        SchoolEntity differentSchool = new SchoolEntity("Different School", new com.hectorherranz.schoolapi.domain.model.valueobject.Capacity(100));
        differentSchool.setId(differentSchoolId);
        StudentEntity differentStudentEntity = new StudentEntity(studentName, differentSchool);
        differentStudentEntity.setId(studentId);

        // When
        Student result = StudentEntityMapper.toDomain(differentStudentEntity);

        // Then
        assertEquals(differentSchoolId, result.schoolId());
    }
}

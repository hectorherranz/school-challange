package com.hectorherranz.schoolapi.adapters.out.jpa.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SchoolEntityMapperTest {

  private UUID schoolId;
  private String schoolName;
  private Capacity capacity;
  private School school;
  private SchoolEntity schoolEntity;

  @BeforeEach
  void setUp() {
    schoolId = UUID.randomUUID();
    schoolName = "Test School";
    capacity = new Capacity(100);
    school = new School(schoolId, schoolName, capacity);
    schoolEntity = new SchoolEntity(schoolName, capacity);
    schoolEntity.setId(schoolId);
  }

  @Test
  void shouldMapDomainToEntity() {
    // When
    SchoolEntity result = SchoolEntityMapper.toEntity(school);

    // Then
    assertNotNull(result);
    assertEquals(schoolId, result.getId()); // Domain-generated UUID is preserved
    assertEquals(schoolName, result.getName());
    assertEquals(capacity, result.getCapacity());
  }

  @Test
  void shouldMapEntityToDomain() {
    // When
    School result = SchoolEntityMapper.toDomain(schoolEntity);

    // Then
    assertNotNull(result);
    assertEquals(schoolId, result.id());
    assertEquals(schoolName, result.name());
    assertEquals(capacity, result.capacity());
    assertEquals(0, result.enrolledCount()); // No students in entity
  }

  @Test
  void shouldHandleNullValues() {
    // Given
    SchoolEntity nullEntity = null;
    School nullSchool = null;

    // When & Then
    assertThrows(NullPointerException.class, () -> SchoolEntityMapper.toDomain(nullEntity));
    assertThrows(NullPointerException.class, () -> SchoolEntityMapper.toEntity(nullSchool));
  }

  @Test
  void shouldPreserveDomainGeneratedId() {
    // Given
    UUID customId = UUID.randomUUID();
    school = new School(customId, schoolName, capacity);

    // When
    SchoolEntity result = SchoolEntityMapper.toEntity(school);

    // Then
    assertEquals(customId, result.getId()); // Domain-generated UUID is preserved
  }

  @Test
  void shouldPreserveCapacityWhenMapping() {
    // Given
    Capacity customCapacity = new Capacity(500);
    school = new School(schoolId, schoolName, customCapacity);

    // When
    SchoolEntity result = SchoolEntityMapper.toEntity(school);

    // Then
    assertEquals(customCapacity, result.getCapacity());
  }
}

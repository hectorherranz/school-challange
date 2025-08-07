package com.hectorherranz.schoolapi.adapters.out.jpa.entity;

import com.hectorherranz.schoolapi.adapters.out.jpa.converter.CapacityConverter;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "schools")
@EntityListeners(AuditingEntityListener.class)
public class SchoolEntity {

  @Id
  @Column(name = "id", columnDefinition = "uuid")
  private UUID id;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Column(name = "capacity", nullable = false)
  @Convert(converter = CapacityConverter.class)
  private Capacity capacity;

  @OneToMany(
      mappedBy = "school",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @MapKey(name = "id")
  @LazyCollection(LazyCollectionOption.EXTRA)
  private Map<UUID, StudentEntity> studentsById = new HashMap<>();

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @Version
  @Column(name = "version")
  private Long version;

  // Default constructor for JPA
  protected SchoolEntity() {}

  // Constructor for creating new schools
  public SchoolEntity(String name, Capacity capacity) {
    this.name = name;
    this.capacity = capacity;
  }

  // Getters
  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Capacity getCapacity() {
    return capacity;
  }

  public Map<UUID, StudentEntity> getStudentsById() {
    return new HashMap<>(studentsById);
  }

  public List<StudentEntity> getStudents() {
    return new ArrayList<>(studentsById.values());
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public Long getVersion() {
    return version;
  }

  // Setters
  public void setId(UUID id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCapacity(Capacity capacity) {
    this.capacity = capacity;
  }

  public void setStudentsById(Map<UUID, StudentEntity> studentsById) {
    this.studentsById = studentsById;
  }

  public void setStudents(List<StudentEntity> students) {
    this.studentsById.clear();
    for (StudentEntity student : students) {
      this.studentsById.put(student.getId(), student);
    }
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
}

package com.hectorherranz.schoolapi.adapters.out.jpa.entity;

import com.hectorherranz.schoolapi.adapters.out.jpa.converter.CapacityConverter;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

  @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<StudentEntity> students = new ArrayList<>();

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

  public List<StudentEntity> getStudents() {
    return new ArrayList<>(students);
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

  public void setStudents(List<StudentEntity> students) {
    this.students = students;
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

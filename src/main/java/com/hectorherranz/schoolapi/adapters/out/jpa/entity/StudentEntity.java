package com.hectorherranz.schoolapi.adapters.out.jpa.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "students")
@EntityListeners(AuditingEntityListener.class)
public class StudentEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "fk_student_school"))
    private SchoolEntity school;

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
    protected StudentEntity() {}

    // Constructor for creating new students
    public StudentEntity(String name, SchoolEntity school) {
        this.name = name;
        this.school = school;
    }

    // Constructor for mapper
    public StudentEntity(UUID id, String name, SchoolEntity school) {
        this.id = id;
        this.name = name;
        this.school = school;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public SchoolEntity getSchool() { return school; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSchool(SchoolEntity school) { this.school = school; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public void setVersion(Long version) { this.version = version; }


}

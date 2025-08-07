package com.hectorherranz.schoolapi.adapters.out.jpa.mapper;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.entity.StudentEntity;
import com.hectorherranz.schoolapi.domain.model.Student;

public final class StudentEntityMapper {

    private StudentEntityMapper() {}

    /* ---------- ENTITY → DOMAIN ---------- */
    public static Student toDomain(StudentEntity e) {
        if (e.getSchool() == null) {
            throw new IllegalStateException("Student entity must have a school reference");
        }
        return new Student(e.getId(), e.getName(), e.getSchool().getId());
    }

    /* ---------- DOMAIN → ENTITY (cascade path) ---------- */
    /** Used by SchoolEntityMapper; school is set afterwards. */
    public static StudentEntity toEntity(Student d) {
        return new StudentEntity(d.id(), d.name(), null);   // parent will inject school
    }

    /* ---------- DOMAIN → ENTITY with explicit school ---------- */
    public static StudentEntity toEntity(Student d, SchoolEntity school) {
        return new StudentEntity(d.id(), d.name(), school);
    }
}

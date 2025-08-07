package com.hectorherranz.schoolapi.adapters.out.jpa.mapper;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.SchoolEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.entity.StudentEntity;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.Student;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class SchoolEntityMapper {

  private SchoolEntityMapper() {}

  /* ENTITY → DOMAIN */
  public static School toDomain(SchoolEntity e) {
    if (e == null) throw new NullPointerException("SchoolEntity cannot be null");

    List<Student> students =
        e.getStudents() == null
            ? Collections.emptyList()
            : e.getStudents().stream().map(StudentEntityMapper::toDomain).toList();

    return School.rehydrate(e.getId(), e.getName(), e.getCapacity(), students);
  }

  /* DOMAIN → ENTITY  (cascades students) */
  public static SchoolEntity toEntity(School s) {
    if (s == null) throw new NullPointerException("School cannot be null");

    SchoolEntity entity = new SchoolEntity(s.name(), s.capacity());
    entity.setId(s.id()); // preserve domain-generated UUID

    List<StudentEntity> studentEntities =
        s.students().stream()
            .map(StudentEntityMapper::toEntity)
            .collect(Collectors.toCollection(ArrayList::new));

    studentEntities.forEach(stu -> stu.setSchool(entity));
    entity.setStudents(studentEntities);

    return entity;
  }
}

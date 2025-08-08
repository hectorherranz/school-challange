package com.hectorherranz.schoolapi.domain.model;

import com.hectorherranz.schoolapi.domain.event.StudentEnrolledEvent;
import com.hectorherranz.schoolapi.domain.exception.CapacityExceededException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.draft.StudentDraft;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Aggregate root : School */
public class School extends AggregateRoot {

  private final UUID id;
  private String name;
  private Capacity capacity;
  private final Map<UUID, Student> studentsById = new HashMap<>();

  /* ---------- constructors ---------- */

  /** Public constructor for NEW schools */
  public School(UUID id, String name, Capacity capacity) {
    this.id = id;
    this.name = name.trim();
    this.capacity = capacity;
  }

  /** Constructor for JPA mapper rehydration */
  public static School rehydrate(UUID id, String name, Capacity capacity, List<Student> existing) {
    School school = new School(id, name, capacity);
    for (Student student : existing) {
      school.studentsById.put(student.id(), student);
    }
    return school;
  }

  /** Constructor for selective loading (only basic school data) */
  public static School rehydrateBasic(UUID id, String name, Capacity capacity) {
    return new School(id, name, capacity);
  }

  /* ---------- business operations ---------- */

  public Student enrollStudent(StudentDraft draft) {
    if (!capacity.canEnroll(studentsById.size())) throw new CapacityExceededException(id);

    UUID studentId = UUID.randomUUID();
    Student s = new Student(studentId, draft.name(), this.id);
    studentsById.put(studentId, s);

    registerEvent(new StudentEnrolledEvent(studentId, this.id));
    return s;
  }

  public void updateStudent(UUID studentId, String newName) {
    Student student = studentsById.get(studentId);
    if (student == null) {
      throw new NotFoundException("Student", studentId.toString());
    }

    // Create updated student with new name
    Student updatedStudent = new Student(studentId, newName, this.id);
    studentsById.put(studentId, updatedStudent);
  }

  /** Optimized method for updating a specific student without loading all students */
  public void updateStudentSelective(UUID studentId, String newName, Student existingStudent) {
    // Validate the student belongs to this school
    if (!existingStudent.schoolId().equals(this.id)) {
      throw new NotFoundException("Student", "Student does not belong to this school");
    }

    // Create updated student with new name
    Student updatedStudent = new Student(studentId, newName, this.id);
    studentsById.put(studentId, updatedStudent);
  }

  public void removeStudent(UUID studentId) {
    Student student = studentsById.get(studentId);
    if (student == null) {
      throw new NotFoundException("Student", studentId.toString());
    }

    studentsById.remove(studentId);
  }

  public Student findStudentById(UUID studentId) {
    return studentsById.get(studentId);
  }

  public void changeName(String newName) {
    this.name = newName.trim();
  }

  public void resizeCapacity(Capacity newCapacity) {
    if (newCapacity.value() < studentsById.size())
      throw new IllegalArgumentException("New capacity cannot be less than current enrollment");
    this.capacity = newCapacity;
  }

  /* ---------- getters ---------- */

  public UUID id() {
    return id;
  }

  public String name() {
    return name;
  }

  public Capacity capacity() {
    return capacity;
  }

  public int enrolledCount() {
    return studentsById.size();
  }

  public List<Student> students() {
    return Collections.unmodifiableList(new ArrayList<>(studentsById.values()));
  }

  public Map<UUID, Student> studentsById() {
    return Collections.unmodifiableMap(new HashMap<>(studentsById));
  }
}

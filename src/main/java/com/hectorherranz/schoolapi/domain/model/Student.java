package com.hectorherranz.schoolapi.domain.model;

/** Entity : Student (owned by School) */
public class Student {

  private final java.util.UUID id;
  private final String name;
  private final java.util.UUID schoolId;

  public Student(java.util.UUID id, String name, java.util.UUID schoolId) {
    if (id == null) throw new NullPointerException("Student ID cannot be null");
    if (name == null) throw new NullPointerException("Student name cannot be null");
    if (schoolId == null) throw new NullPointerException("School ID cannot be null");

    this.id = id;
    this.name = name.trim();
    this.schoolId = schoolId;
  }

  /* ---------- getters ---------- */

  public java.util.UUID id() {
    return id;
  }

  public String name() {
    return name;
  }

  public java.util.UUID schoolId() {
    return schoolId;
  }
}

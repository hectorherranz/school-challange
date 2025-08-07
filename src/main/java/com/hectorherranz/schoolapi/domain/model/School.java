package com.hectorherranz.schoolapi.domain.model;

import com.hectorherranz.schoolapi.domain.event.StudentEnrolledEvent;
import com.hectorherranz.schoolapi.domain.exception.CapacityExceededException;
import com.hectorherranz.schoolapi.domain.model.draft.StudentDraft;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/** Aggregate root : School */
public class School extends AggregateRoot {

    private final UUID id;
    private       String name;
    private       Capacity capacity;
    private final List<Student> students = new ArrayList<>();

    /* ---------- constructors ---------- */

    /** New school factory */
    public School(UUID id, String name, Capacity capacity) {
        this.id       = id;
        this.name     = name.trim();
        this.capacity = capacity;
    }

    /* ---------- business operations ---------- */

    public Student enrollStudent(StudentDraft draft) {
        if (!capacity.canEnroll(students.size()))
            throw new CapacityExceededException(id);

        UUID studentId = UUID.randomUUID();
        Student s = new Student(studentId, draft.name(), this.id);
        students.add(s);

        registerEvent(new StudentEnrolledEvent(studentId, this.id));
        return s;
    }

    public void changeName(String newName) {
        this.name = newName.trim();
    }

    public void resizeCapacity(Capacity newCapacity) {
        if (newCapacity.value() < students.size())
            throw new IllegalArgumentException("New capacity cannot be less than current enrollment");
        this.capacity = newCapacity;
    }

    /* ---------- getters ---------- */

    public UUID id()                 { return id; }
    public String name()             { return name; }
    public Capacity capacity()       { return capacity; }
    public int enrolledCount()       { return students.size(); }
    public List<Student> students()  { return Collections.unmodifiableList(students); }
}

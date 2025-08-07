package com.hectorherranz.schoolapi.domain.repository;

import com.hectorherranz.schoolapi.domain.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepositoryPort {
    Optional<Student> findById(UUID id);
    Page<Student> search(UUID schoolId, String q, Pageable pageable);
    Student save(Student student);       // usually cascaded via School, but handy for updates
    void deleteById(UUID id);
}

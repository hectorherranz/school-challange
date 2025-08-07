package com.hectorherranz.schoolapi.adapters.out.jpa.repository;

import com.hectorherranz.schoolapi.adapters.out.jpa.entity.StudentEntity;
import com.hectorherranz.schoolapi.adapters.out.jpa.mapper.StudentEntityMapper;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
class StudentJpaRepository implements StudentRepositoryPort {

    private final SpringDataStudentRepository repo;

    public StudentJpaRepository(SpringDataStudentRepository repo) {
        this.repo = repo;
    }

    /* ---------- Reads ---------- */

    @Override
    public Optional<Student> findById(UUID id) {
        return repo.findById(id).map(StudentEntityMapper::toDomain);
    }

    @Override
    public Page<Student> search(UUID schoolId, String q, Pageable pageable) {
        return repo.findBySchoolIdAndNameContainingIgnoreCase(schoolId, q, pageable)
                   .map(StudentEntityMapper::toDomain);
    }

    /* ---------- Writes (update only) ---------- */

    @Transactional
    @Override
    public Student save(Student student) {
        StudentEntity entity = repo.findById(student.id())
                .orElseThrow(() -> new NotFoundException("Student", student.id().toString()));

        entity.setName(student.name());                // mutate allowed fields
        StudentEntity saved = repo.save(entity);
        return StudentEntityMapper.toDomain(saved);
    }

    @Transactional
    @Override
    public void deleteById(UUID id) {
        repo.deleteById(id);
    }
}

package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.DeleteStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.DeleteStudentUseCase;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DeleteStudentHandler implements DeleteStudentUseCase {

    private final StudentRepositoryPort studentRepository;

    public DeleteStudentHandler(StudentRepositoryPort studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void handle(DeleteStudentCommand command) {
        // Check if student exists before deleting
        if (!studentRepository.findById(command.studentId()).isPresent()) {
            throw new NotFoundException("Student", command.studentId().toString());
        }

        // Delete the student
        studentRepository.deleteById(command.studentId());
    }
}

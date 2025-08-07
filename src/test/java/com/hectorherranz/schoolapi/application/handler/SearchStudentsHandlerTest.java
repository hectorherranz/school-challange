package com.hectorherranz.schoolapi.application.handler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.hectorherranz.schoolapi.application.query.SearchStudentsQuery;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class SearchStudentsHandlerTest {

  @Mock private StudentRepositoryPort studentRepository;

  private SearchStudentsHandler handler;

  @BeforeEach
  void setUp() {
    handler = new SearchStudentsHandler(studentRepository);
  }

  @Test
  void handle_ValidQuery_ReturnsPagedResponse() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String searchQuery = "Harry";
    Pageable pageable = PageRequest.of(0, 10);
    SearchStudentsQuery query = new SearchStudentsQuery(schoolId, searchQuery, pageable);

    List<Student> students =
        List.of(
            new Student(UUID.randomUUID(), "Harry Potter", schoolId),
            new Student(UUID.randomUUID(), "Harry Styles", schoolId));
    Page<Student> page = new PageImpl<>(students, pageable, 2);

    when(studentRepository.search(schoolId, searchQuery, pageable)).thenReturn(page);

    // Act
    PagedResponse<Student> result = handler.handle(query);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.content().size());
    assertEquals(0, result.pageNumber());
    assertEquals(10, result.pageSize());
    assertEquals(2, result.totalElements());
    assertEquals(1, result.totalPages());
    assertFalse(result.hasNext());
    assertFalse(result.hasPrevious());
    verify(studentRepository).search(schoolId, searchQuery, pageable);
  }

  @Test
  void handle_EmptySearchQuery_ReturnsAllStudentsInSchool() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String searchQuery = "";
    Pageable pageable = PageRequest.of(0, 5);
    SearchStudentsQuery query = new SearchStudentsQuery(schoolId, searchQuery, pageable);

    List<Student> students =
        List.of(
            new Student(UUID.randomUUID(), "Student 1", schoolId),
            new Student(UUID.randomUUID(), "Student 2", schoolId),
            new Student(UUID.randomUUID(), "Student 3", schoolId));
    Page<Student> page = new PageImpl<>(students, pageable, 3);

    when(studentRepository.search(schoolId, searchQuery, pageable)).thenReturn(page);

    // Act
    PagedResponse<Student> result = handler.handle(query);

    // Assert
    assertEquals(3, result.content().size());
    assertEquals(3, result.totalElements());
    verify(studentRepository).search(schoolId, searchQuery, pageable);
  }

  @Test
  void handle_PaginatedResults_ReturnsCorrectPaginationInfo() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String searchQuery = "Student";
    Pageable pageable = PageRequest.of(1, 2); // Second page, 2 items per page
    SearchStudentsQuery query = new SearchStudentsQuery(schoolId, searchQuery, pageable);

    List<Student> students =
        List.of(
            new Student(UUID.randomUUID(), "Student 3", schoolId),
            new Student(UUID.randomUUID(), "Student 4", schoolId));
    Page<Student> page = new PageImpl<>(students, pageable, 5); // Total 5 items

    when(studentRepository.search(schoolId, searchQuery, pageable)).thenReturn(page);

    // Act
    PagedResponse<Student> result = handler.handle(query);

    // Assert
    assertEquals(1, result.pageNumber());
    assertEquals(2, result.pageSize());
    assertEquals(5, result.totalElements());
    assertEquals(3, result.totalPages()); // 5 items / 2 per page = 3 pages
    assertTrue(result.hasNext());
    assertTrue(result.hasPrevious());
  }

  @Test
  void handle_NoResults_ReturnsEmptyPagedResponse() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String searchQuery = "NonExistentStudent";
    Pageable pageable = PageRequest.of(0, 10);
    SearchStudentsQuery query = new SearchStudentsQuery(schoolId, searchQuery, pageable);

    Page<Student> page = new PageImpl<>(List.of(), pageable, 0);

    when(studentRepository.search(schoolId, searchQuery, pageable)).thenReturn(page);

    // Act
    PagedResponse<Student> result = handler.handle(query);

    // Assert
    assertTrue(result.content().isEmpty());
    assertEquals(0, result.totalElements());
    assertEquals(0, result.totalPages());
    assertFalse(result.hasNext());
    assertFalse(result.hasPrevious());
  }

  @Test
  void handle_ValidQuery_CallsRepositoryWithCorrectParameters() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String searchQuery = "Test";
    Pageable pageable = PageRequest.of(0, 20);
    SearchStudentsQuery query = new SearchStudentsQuery(schoolId, searchQuery, pageable);

    Page<Student> page = new PageImpl<>(List.of(), pageable, 0);
    when(studentRepository.search(schoolId, searchQuery, pageable)).thenReturn(page);

    // Act
    handler.handle(query);

    // Assert
    verify(studentRepository).search(eq(schoolId), eq(searchQuery), eq(pageable));
  }

  @Test
  void handle_StudentsFromSpecificSchool_ReturnsOnlySchoolStudents() {
    // Arrange
    UUID schoolId = UUID.randomUUID();
    String searchQuery = "Student";
    Pageable pageable = PageRequest.of(0, 10);
    SearchStudentsQuery query = new SearchStudentsQuery(schoolId, searchQuery, pageable);

    List<Student> students =
        List.of(
            new Student(UUID.randomUUID(), "Student 1", schoolId),
            new Student(UUID.randomUUID(), "Student 2", schoolId));
    Page<Student> page = new PageImpl<>(students, pageable, 2);

    when(studentRepository.search(schoolId, searchQuery, pageable)).thenReturn(page);

    // Act
    PagedResponse<Student> result = handler.handle(query);

    // Assert
    assertEquals(2, result.content().size());
    result.content().forEach(student -> assertEquals(schoolId, student.schoolId()));
    verify(studentRepository).search(schoolId, searchQuery, pageable);
  }
}

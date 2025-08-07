package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.query.SearchSchoolsQuery;
import com.hectorherranz.schoolapi.application.response.PagedResponse;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchSchoolsHandlerTest {

    @Mock
    private SchoolRepositoryPort schoolRepository;

    private SearchSchoolsHandler handler;

    @BeforeEach
    void setUp() {
        handler = new SearchSchoolsHandler(schoolRepository);
    }

    @Test
    void handle_ValidQuery_ReturnsPagedResponse() {
        // Arrange
        String searchQuery = "Hogwarts";
        Pageable pageable = PageRequest.of(0, 10);
        SearchSchoolsQuery query = new SearchSchoolsQuery(searchQuery, pageable);
        
        List<School> schools = List.of(
            new School(UUID.randomUUID(), "Hogwarts School", new Capacity(500)),
            new School(UUID.randomUUID(), "Hogwarts Academy", new Capacity(300))
        );
        Page<School> page = new PageImpl<>(schools, pageable, 2);
        
        when(schoolRepository.searchByName(searchQuery, pageable)).thenReturn(page);

        // Act
        PagedResponse<School> result = handler.handle(query);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(0, result.pageNumber());
        assertEquals(10, result.pageSize());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
        assertFalse(result.hasNext());
        assertFalse(result.hasPrevious());
        verify(schoolRepository).searchByName(searchQuery, pageable);
    }

    @Test
    void handle_EmptySearchQuery_ReturnsAllSchools() {
        // Arrange
        String searchQuery = "";
        Pageable pageable = PageRequest.of(0, 5);
        SearchSchoolsQuery query = new SearchSchoolsQuery(searchQuery, pageable);
        
        List<School> schools = List.of(
            new School(UUID.randomUUID(), "School 1", new Capacity(100)),
            new School(UUID.randomUUID(), "School 2", new Capacity(200)),
            new School(UUID.randomUUID(), "School 3", new Capacity(300))
        );
        Page<School> page = new PageImpl<>(schools, pageable, 3);
        
        when(schoolRepository.searchByName(searchQuery, pageable)).thenReturn(page);

        // Act
        PagedResponse<School> result = handler.handle(query);

        // Assert
        assertEquals(3, result.content().size());
        assertEquals(3, result.totalElements());
        verify(schoolRepository).searchByName(searchQuery, pageable);
    }

    @Test
    void handle_PaginatedResults_ReturnsCorrectPaginationInfo() {
        // Arrange
        String searchQuery = "School";
        Pageable pageable = PageRequest.of(1, 2); // Second page, 2 items per page
        SearchSchoolsQuery query = new SearchSchoolsQuery(searchQuery, pageable);
        
        List<School> schools = List.of(
            new School(UUID.randomUUID(), "School 3", new Capacity(300)),
            new School(UUID.randomUUID(), "School 4", new Capacity(400))
        );
        Page<School> page = new PageImpl<>(schools, pageable, 5); // Total 5 items
        
        when(schoolRepository.searchByName(searchQuery, pageable)).thenReturn(page);

        // Act
        PagedResponse<School> result = handler.handle(query);

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
        String searchQuery = "NonExistentSchool";
        Pageable pageable = PageRequest.of(0, 10);
        SearchSchoolsQuery query = new SearchSchoolsQuery(searchQuery, pageable);
        
        Page<School> page = new PageImpl<>(List.of(), pageable, 0);
        
        when(schoolRepository.searchByName(searchQuery, pageable)).thenReturn(page);

        // Act
        PagedResponse<School> result = handler.handle(query);

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
        String searchQuery = "Test";
        Pageable pageable = PageRequest.of(0, 20);
        SearchSchoolsQuery query = new SearchSchoolsQuery(searchQuery, pageable);
        
        Page<School> page = new PageImpl<>(List.of(), pageable, 0);
        when(schoolRepository.searchByName(searchQuery, pageable)).thenReturn(page);

        // Act
        handler.handle(query);

        // Assert
        verify(schoolRepository).searchByName(eq(searchQuery), eq(pageable));
    }
}

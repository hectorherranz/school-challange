package com.hectorherranz.schoolapi.application.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious()
        );
    }
}

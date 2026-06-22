package br.edu.ifg.med_clinica_api.domain.dto.pages;

import org.springframework.data.domain.Page;

import java.util.List;

public record SimplePageResponseDTO<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public SimplePageResponseDTO(Page<T> page) {
        this(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}

package com.github.carreiras.libraryapi.service;


import com.github.carreiras.libraryapi.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Book save(Book any);

    Book update(Book book);

    void delete(Book book);

    Optional<Book> findById(Long id);

    Page<Book> find(Book filter, Pageable pageRequest);
}

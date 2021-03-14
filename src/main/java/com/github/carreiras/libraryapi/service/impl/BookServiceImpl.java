package com.github.carreiras.libraryapi.service.impl;

import com.github.carreiras.libraryapi.api.exception.BusinessException;
import com.github.carreiras.libraryapi.model.entity.Book;
import com.github.carreiras.libraryapi.model.repository.BookRepository;
import com.github.carreiras.libraryapi.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository repository) {
        this.bookRepository = repository;
    }

    @Override
    public Book save(Book book) {
        existsByIsbn(book);
        return bookRepository.save(book);
    }

    @Override
    public Book update(Book book) {
        bookIsNull(book);
        return bookRepository.save(book);
    }

    @Override
    public void delete(Book book) {
        bookIsNull(book);
        bookRepository.delete(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        Example<Book> bookExample = Example.of(filter, ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return bookRepository.findAll(bookExample, pageRequest);
    }

    private void existsByIsbn(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn()))
            throw new BusinessException("Isbn já cadastrado.");
    }

    private void bookIsNull(Book book) {
        if (book == null || book.getId() == null)
            throw new IllegalArgumentException("O Id do livro não pode ser nulo.");
    }
}

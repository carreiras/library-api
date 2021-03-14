package com.github.carreiras.libraryapi.service;

import com.github.carreiras.libraryapi.api.exception.BusinessException;
import com.github.carreiras.libraryapi.model.entity.Book;
import com.github.carreiras.libraryapi.model.repository.BookRepository;
import com.github.carreiras.libraryapi.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        Book book = createBook();
        when(bookRepository.save(any()))
                .thenReturn(Book.builder().id(1l).title("Livro").author("Autor").isbn("001").build());

        Book savedBook = bookService.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Livro");
        assertThat(savedBook.getAuthor()).isEqualTo("Autor");
        assertThat(savedBook.getIsbn()).isEqualTo("001");
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN() {
        Book book = Book.builder().title("Livro").author("Autor").isbn("001").build();
        when(bookRepository.existsByIsbn(anyString())).thenReturn(true);

        Throwable exception = catchThrowable(() -> bookService.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        verify(bookRepository, never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por Id.")
    public void findByIdTest() {
        Book book = createBook();
        book.setId(1l);
        when(bookRepository.findById(1l)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.findById(1l);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(1l);
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base")
    public void findByIdNotFoundTest() {
        when(bookRepository.findById(1l)).thenReturn(Optional.empty());

        Optional<Book> foundBook = bookService.findById(1l);

        assertThat(foundBook.isPresent()).isFalse();
    }


    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() {
        Book book = Book.builder().id(1l).build();

        assertDoesNotThrow(() -> bookService.delete(book));

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente")
    public void deleteInvalidBookTest() {
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));

        verify(bookRepository, never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest() {
        Book updatingBook = Book.builder().id(1l).build();

        Book updatedBook = createBook();
        when(bookRepository.save(updatingBook)).thenReturn(updatedBook);

        Book book = bookService.update(updatingBook);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente.")
    public void updateInvalidBookTest() {
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

        verify(bookRepository, never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        Book book = createBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = Arrays.asList(book);
        PageImpl<Book> page = new PageImpl<>(lista, PageRequest.of(0, 10), 1);
        when(bookRepository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(page);

        Page<Book> result = bookService.find(book, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Book createBook() {
        return Book.builder().title("Livro").author("Autor").isbn("001").build();
    }
}
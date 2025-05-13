package com.getir.bootcamp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.bootcamp.dto.request.BookRequest;
import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities = "ROLE_LIBRARIAN")
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void addBook_ShouldReturnBook() throws Exception {
        BookRequest bookRequest = new BookRequest(
                "Effective Java",
                "Joshua Bloch",
                "9780134685991",
                LocalDate.of(2018, 1, 6),
                "Programming"
        );

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Effective Java"))
                .andExpect(jsonPath("$.data.isAvailable").value(true));

        assertEquals(1, bookRepository.count());
    }

    @Test
    void getBookById_ShouldReturnBook() throws Exception {
        Book book = Book.builder()
                .title("Refactoring")
                .author("Martin Fowler")
                .isbn("9780201485677")
                .publicationDate(LocalDate.of(1999, 7, 8))
                .genre("Programming")
                .isAvailable(true)
                .build();
        Book savedBook = bookRepository.save(book);

        mockMvc.perform(get("/api/v1/books/" + savedBook.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Refactoring"));
    }

    @Test
    void updateBook_ShouldUpdateAndReturnBook() throws Exception {
        Book existingBook = bookRepository.save(Book.builder()
                .title("Old Title")
                .author("Author")
                .isbn("1234567890")
                .publicationDate(LocalDate.of(2020, 1, 1))
                .genre("Tech")
                .isAvailable(true)
                .build());

        BookRequest updateRequest = new BookRequest(
                "New Title",
                "Author",
                "1234567890",
                LocalDate.of(2020, 1, 1),
                "Tech"
        );

        mockMvc.perform(put("/api/v1/books/" + existingBook.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("New Title"));
    }

    @Test
    void deleteBook_ShouldRemoveBook() throws Exception {
        Book book = bookRepository.save(Book.builder()
                .title("To Be Deleted")
                .author("Author")
                .isbn("1111111111")
                .publicationDate(LocalDate.of(2000, 1, 1))
                .genre("Misc")
                .isAvailable(true)
                .build());

        mockMvc.perform(delete("/api/v1/books/" + book.getId()))
                .andExpect(status().isOk());

        assertEquals(0, bookRepository.count());
    }

    @Test
    void searchBooks_ShouldReturnResults() throws Exception {
        bookRepository.save(Book.builder()
                .title("Spring Boot Guide")
                .author("Jane Doe")
                .isbn("9999999999")
                .publicationDate(LocalDate.of(2022, 4, 1))
                .genre("Programming")
                .isAvailable(true)
                .build());

        mockMvc.perform(get("/api/v1/books/search")
                        .param("keyword", "Spring")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title").value("Spring Boot Guide"));
    }
}

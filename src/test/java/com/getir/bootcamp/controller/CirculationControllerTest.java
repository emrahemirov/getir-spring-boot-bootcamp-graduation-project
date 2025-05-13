package com.getir.bootcamp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.getir.bootcamp.dto.request.CirculationRequest;
import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.entity.Circulation;
import com.getir.bootcamp.entity.Role;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.repository.BookRepository;
import com.getir.bootcamp.repository.CirculationRepository;
import com.getir.bootcamp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CirculationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private CirculationRepository circulationRepository;

    private User patronUser;
    private Book availableBook;


    @BeforeEach
    void setUp() {
        circulationRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();

        patronUser = new User();
        patronUser.setUsername("patron");
        patronUser.setPassword("encoded");
        patronUser.setFirstName("Jane");
        patronUser.setLastName("Doe");
        patronUser.setCanBorrow(true);
        patronUser.setRole(Role.ROLE_PATRON);
        patronUser = userRepository.save(patronUser);

        availableBook = Book.builder()
                .title("Test Book")
                .author("Author")
                .isbn("1234567890123")
                .publicationDate(LocalDate.of(2020, 1, 1))
                .genre("Fiction")
                .isAvailable(true)
                .build();
        availableBook = bookRepository.save(availableBook);

        SecurityContextHolder.clearContext();
        userRepository.save(patronUser);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(patronUser, null, List.of(() -> "ROLE_PATRON"));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    @WithMockUser(username = "patron", authorities = "ROLE_PATRON")
    void borrowBook_ShouldCreateCirculation() throws Exception {
        CirculationRequest request = new CirculationRequest(
                availableBook.getId(),
                LocalDate.now(),
                LocalDate.now().plusDays(14)
        );

        mockMvc.perform(post("/api/v1/circulations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.book.title").value("Test Book"));

        assertThat(circulationRepository.count()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "patron", authorities = "ROLE_PATRON")
    void returnBook_ShouldUpdateReturnDate() throws Exception {
        Circulation circulation = circulationRepository.save(Circulation.builder()
                .user(patronUser)
                .book(availableBook)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .build());

        mockMvc.perform(post("/api/v1/circulations/" + circulation.getId() + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.returnDate").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "patron", authorities = "ROLE_PATRON")
    void getMyHistory_ShouldReturnBorrowRecords() throws Exception {
        circulationRepository.save(Circulation.builder()
                .user(patronUser)
                .book(availableBook)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(5))
                .build());

        mockMvc.perform(get("/api/v1/circulations/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].book.title").value("Test Book"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_LIBRARIAN")
    void getAllHistory_ShouldReturnAllCirculations() throws Exception {
        circulationRepository.save(Circulation.builder()
                .user(patronUser)
                .book(availableBook)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(3))
                .build());

        mockMvc.perform(get("/api/v1/circulations/history/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].book.title").value("Test Book"));
    }

    @Test
    @WithMockUser(authorities = "ROLE_LIBRARIAN")
    void getOverdueBooks_ShouldReturnList() throws Exception {
        circulationRepository.save(Circulation.builder()
                .user(patronUser)
                .book(availableBook)
                .borrowDate(LocalDate.now().minusDays(10))
                .dueDate(LocalDate.now().minusDays(5))
                .build());

        mockMvc.perform(get("/api/v1/circulations/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].book.title").value("Test Book"));
    }
}

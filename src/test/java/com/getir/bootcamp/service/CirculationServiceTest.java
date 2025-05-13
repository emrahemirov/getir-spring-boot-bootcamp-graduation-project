package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.request.CirculationRequest;
import com.getir.bootcamp.dto.response.BookResponse;
import com.getir.bootcamp.dto.response.CirculationResponse;
import com.getir.bootcamp.dto.response.UserResponse;
import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.entity.Circulation;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.exception.BadRequestException;
import com.getir.bootcamp.exception.ConflictException;
import com.getir.bootcamp.exception.ExceptionMessages;
import com.getir.bootcamp.mapper.CirculationMapper;
import com.getir.bootcamp.repository.CirculationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CirculationServiceTest {

    @Mock
    private CirculationRepository circulationRepository;
    @Mock
    private BookService bookService;
    @Mock
    private UserService userService;
    @Mock
    private CirculationMapper circulationMapper;

    @InjectMocks
    private CirculationService circulationService;

    private User user;
    private Book book;
    private CirculationRequest request;
    private Circulation circulation;
    private CirculationResponse response;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("john_doe");
        user.setCanBorrow(true);

        book = new Book();
        book.setId(101L);
        book.setIsAvailable(true);

        request = new CirculationRequest(
                101L,
                LocalDate.now(),
                LocalDate.now().plusDays(10)
        );

        circulation = Circulation.builder()
                .id(1L)
                .user(user)
                .book(book)
                .borrowDate(request.borrowDate())
                .dueDate(request.dueDate())
                .build();

        response = new CirculationResponse(
                1L,
                new UserResponse("john_doe", "John", "Doe", true, null),
                new BookResponse(101L, "Book", "Author", "123456789", LocalDate.of(2020, 1, 1), true, "Fiction"),
                request.borrowDate(),
                request.dueDate(),
                null,
                false,
                false
        );
    }

    @Test
    void borrowBook_ShouldSucceed() {
        when(bookService.getBookEntityById(101L)).thenReturn(book);
        when(userService.getUserEntityByUsername("john_doe")).thenReturn(user);
        when(circulationRepository.save(any())).thenReturn(circulation);
        when(circulationRepository.findById(1L)).thenReturn(Optional.of(circulation));
        when(circulationMapper.ciraculationEntityToCirculationResponse(circulation)).thenReturn(response);

        CirculationResponse result = circulationService.borrowBook("john_doe", request);

        assertEquals(response.id(), result.id());
        verify(bookService).setBookAvailability(book, false);
        verify(userService).setUserBorrowability(user, false);
    }

    @Test
    void borrowBook_ShouldFail_WhenBookNotAvailable() {
        book.setIsAvailable(false);
        when(bookService.getBookEntityById(101L)).thenReturn(book);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> circulationService.borrowBook("john_doe", request));
        assertEquals(ExceptionMessages.BOOK_NOT_AVAILABLE, ex.getMessage());
    }

    @Test
    void borrowBook_ShouldFail_WhenDueDateBeforeBorrowDate() {
        CirculationRequest invalidRequest = new CirculationRequest(
                101L,
                LocalDate.now(),
                LocalDate.now().minusDays(1)
        );
        when(bookService.getBookEntityById(101L)).thenReturn(book);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> circulationService.borrowBook("john_doe", invalidRequest));
        assertEquals(ExceptionMessages.DUE_DATE_MUST_BE_AFTER_BORROW_DATE, ex.getMessage());
    }

    @Test
    void borrowBook_ShouldFail_WhenUserCannotBorrow() {
        user.setCanBorrow(false);
        when(bookService.getBookEntityById(101L)).thenReturn(book);
        when(userService.getUserEntityByUsername("john_doe")).thenReturn(user);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> circulationService.borrowBook("john_doe", request));
        assertEquals(ExceptionMessages.USER_CANNOT_BORROW, ex.getMessage());
    }

    @Test
    void returnBook_ShouldSucceed() {
        Circulation unreturned = Circulation.builder()
                .id(1L)
                .book(book)
                .user(user)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(7))
                .returnDate(null)
                .build();

        when(circulationRepository.findById(1L)).thenReturn(Optional.of(unreturned));
        when(circulationRepository.save(any())).thenReturn(unreturned);
        when(circulationMapper.ciraculationEntityToCirculationResponse(unreturned)).thenReturn(response);

        CirculationResponse result = circulationService.returnBook(1L);

        assertNotNull(result);
        verify(bookService).setBookAvailability(book, true);
        verify(userService).setUserBorrowability(user, true);
    }

    @Test
    void returnBook_ShouldFail_WhenAlreadyReturned() {
        Circulation returned = Circulation.builder()
                .id(1L)
                .returnDate(LocalDate.now())
                .build();

        when(circulationRepository.findById(1L)).thenReturn(Optional.of(returned));

        ConflictException ex = assertThrows(ConflictException.class,
                () -> circulationService.returnBook(1L));
        assertEquals(ExceptionMessages.BOOK_ALREADY_RETURNED, ex.getMessage());
    }

    @Test
    void getUserHistory_ShouldReturnMappedList() {
        when(circulationRepository.findByUsernameWithBookAndUser("john_doe")).thenReturn(List.of(circulation));
        when(circulationMapper.ciraculationEntityToCirculationResponse(circulation)).thenReturn(response);

        List<CirculationResponse> result = circulationService.getUserHistory("john_doe");

        assertEquals(1, result.size());
    }

    @Test
    void getAllCirculations_ShouldReturnMappedList() {
        when(circulationRepository.findAllWithBookAndUser()).thenReturn(List.of(circulation));
        when(circulationMapper.ciraculationEntityToCirculationResponse(circulation)).thenReturn(response);

        List<CirculationResponse> result = circulationService.getAllCirculations();

        assertEquals(1, result.size());
    }

    @Test
    void getOverdueBooks_ShouldReturnMappedList() {
        when(circulationRepository.findOverdueWithBookAndUser(any())).thenReturn(List.of(circulation));
        when(circulationMapper.ciraculationEntityToCirculationResponse(circulation)).thenReturn(response);

        List<CirculationResponse> result = circulationService.getOverdueBooks();

        assertEquals(1, result.size());
    }
}

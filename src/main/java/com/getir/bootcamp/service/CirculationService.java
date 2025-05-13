package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.request.CirculationRequest;
import com.getir.bootcamp.dto.response.CirculationResponse;
import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.entity.Circulation;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.exception.BadRequestException;
import com.getir.bootcamp.exception.ConflictException;
import com.getir.bootcamp.exception.ExceptionMessages;
import com.getir.bootcamp.exception.ResourceNotFoundException;
import com.getir.bootcamp.mapper.CirculationMapper;
import com.getir.bootcamp.repository.CirculationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CirculationService {
    private final CirculationRepository circulationRepository;
    private final BookService bookService;
    private final UserService userService;
    private final CirculationMapper circulationMapper;

    public CirculationResponse borrowBook(String username, CirculationRequest request) {
        Book book = bookService.getBookEntityById(request.bookId());

        if (Boolean.FALSE.equals(book.getIsAvailable())) {
            throw new BadRequestException(ExceptionMessages.BOOK_NOT_AVAILABLE);
        }

        if (request.dueDate().isBefore(request.borrowDate())) {
            throw new BadRequestException(ExceptionMessages.DUE_DATE_MUST_BE_AFTER_BORROW_DATE);
        }

        User user = userService.getUserEntityByUsername(username);

        if (Boolean.FALSE.equals(user.getCanBorrow())) {
            throw new BadRequestException(ExceptionMessages.USER_CANNOT_BORROW);
        }

        Circulation circulation = Circulation.builder()
                .user(user)
                .book(book)
                .borrowDate(request.borrowDate())
                .dueDate(request.dueDate())
                .build();

        Circulation saved = circulationRepository.save(circulation);

        Circulation loaded = circulationRepository.findById(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.CIRCULATION_NOT_FOUND));

        userService.setUserBorrowability(user, false);
        bookService.setBookAvailability(book, false);

        return circulationMapper.ciraculationEntityToCirculationResponse(loaded);
    }

    public CirculationResponse returnBook(Long circulationId) {
        Circulation circulation = circulationRepository.findById(circulationId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.CIRCULATION_NOT_FOUND));

        if (circulation.isReturned()) {
            throw new ConflictException(ExceptionMessages.BOOK_ALREADY_RETURNED);
        }


        circulation.setReturnDate(LocalDate.now());
        Circulation updated = circulationRepository.save(circulation);


        userService.setUserBorrowability(circulation.getUser(), true);
        bookService.setBookAvailability(circulation.getBook(), true);

        return circulationMapper.ciraculationEntityToCirculationResponse(updated);
    }

    public List<CirculationResponse> getUserHistory(String username) {
        List<Circulation> records = circulationRepository.findByUsernameWithBookAndUser(username);
        return records.stream().map(circulationMapper::ciraculationEntityToCirculationResponse).toList();
    }

    public List<CirculationResponse> getAllCirculations() {
        List<Circulation> allCirculations = circulationRepository.findAllWithBookAndUser();
        return allCirculations.stream()
                .map(circulationMapper::ciraculationEntityToCirculationResponse)
                .toList();
    }

    public List<CirculationResponse> getOverdueBooks() {
        List<Circulation> overdue = circulationRepository.findOverdueWithBookAndUser(LocalDate.now());
        return overdue.stream().map(circulationMapper::ciraculationEntityToCirculationResponse).toList();
    }
}

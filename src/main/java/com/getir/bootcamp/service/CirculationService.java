package com.getir.bootcamp.service;

import com.getir.bootcamp.dto.request.CirculationRequest;
import com.getir.bootcamp.dto.response.CirculationResponse;
import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.entity.Circulation;
import com.getir.bootcamp.entity.User;
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

    private static final int BORROW_DAYS = 14;
    private CirculationRepository circulationRepository;
    private BookService bookService;
    private UserService userService;
    private CirculationMapper circulationMapper;

    public CirculationResponse borrowBook(Long userId, CirculationRequest request) {
        Book book = bookService.getBookEntityById(request.bookId());

        if (!bookService.isBookAvailable(book.getId())) {
            throw new IllegalStateException(ExceptionMessages.BOOK_NOT_AVAILABLE);
        }

        User user = userService.getUserEntityById(userId);

        Circulation circulation = Circulation.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(BORROW_DAYS))
                .build();

        Circulation saved = circulationRepository.save(circulation);

        // Refetch with book + user eagerly loaded
        Circulation loaded = circulationRepository.findByIdWithBookAndUser(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.CIRCULATION_NOT_FOUND));

        return circulationMapper.ciraculationEntityToCirculationResponse(loaded);
    }

    public CirculationResponse returnBook(Long circulationId) {
        Circulation circulation = circulationRepository.findByIdWithBookAndUser(circulationId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.CIRCULATION_NOT_FOUND));

        if (circulation.isReturned()) {
            throw new IllegalStateException(ExceptionMessages.BOOK_ALREADY_RETURNED);
        }

        circulation.setReturnDate(LocalDate.now());
        Circulation updated = circulationRepository.save(circulation);
        return circulationMapper.ciraculationEntityToCirculationResponse(updated);
    }

    public List<CirculationResponse> getUserHistory(Long userId) {
        List<Circulation> records = circulationRepository.findByUserIdWithBookAndUser(userId);
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

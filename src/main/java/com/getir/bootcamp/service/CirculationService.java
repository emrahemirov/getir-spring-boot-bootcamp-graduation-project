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
    private CirculationRepository circulationRepository;
    private BookService bookService;
    private UserService userService;
    private CirculationMapper circulationMapper;

    public CirculationResponse borrowBook(Long userId, CirculationRequest request) {
        Book book = bookService.getBookEntityById(request.bookId());

        if (!circulationRepository.isBookAvailable(book.getId())) {
            throw new ConflictException(ExceptionMessages.BOOK_NOT_AVAILABLE);
        }

        if (request.dueDate().isBefore(request.borrowDate())) {
            throw new BadRequestException(ExceptionMessages.DUE_DATE_MUST_BE_AFTER_BORROW_DATE);
        }

        User user = userService.getUserEntityById(userId);

        Circulation circulation = Circulation.builder()
                .user(user)
                .book(book)
                .borrowDate(request.borrowDate())
                .dueDate(request.dueDate())
                .build();

        Circulation saved = circulationRepository.save(circulation);

        Circulation loaded = circulationRepository.findById(saved.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.CIRCULATION_NOT_FOUND));

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

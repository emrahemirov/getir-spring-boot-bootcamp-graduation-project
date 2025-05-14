package com.getir.bootcamp.startup;

import com.getir.bootcamp.entity.Book;
import com.getir.bootcamp.entity.Circulation;
import com.getir.bootcamp.entity.Role;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.repository.BookRepository;
import com.getir.bootcamp.repository.CirculationRepository;
import com.getir.bootcamp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AddStartupEntities implements ApplicationRunner {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookRepository bookRepository;
    private final CirculationRepository circulationRepository;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        userRepository.deleteAll();
        userRepository.save(new User("librarian", passwordEncoder.encode("password"), "librarian", "librarian", true, Role.ROLE_LIBRARIAN, null));
        userRepository.save(new User("patron", passwordEncoder.encode("password"), "patron", "patron", true, Role.ROLE_PATRON, null));
        userRepository.save(new User("username", passwordEncoder.encode("password"), "username", "username", false, Role.ROLE_PATRON, null));


        List<Book> books;

        bookRepository.deleteAll();
        books = bookRepository.saveAll(List.of(
                new Book(null, "The Hobbit", "J.R.R. Tolkien", "9780007525492", LocalDate.of(1937, 9, 21), "Fantasy", true, null),
                new Book(null, "1984", "George Orwell", "9780451524935", LocalDate.of(1949, 6, 8), "Dystopian", true, null),
                new Book(null, "To Kill a Mockingbird", "Harper Lee", "9780061120084", LocalDate.of(1960, 7, 11), "Fiction", true, null),
                new Book(null, "The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", LocalDate.of(1925, 4, 10), "Classic", true, null),
                new Book(null, "Pride and Prejudice", "Jane Austen", "9780141439518", LocalDate.of(1813, 1, 28), "Romance", true, null),
                new Book(null, "The Catcher in the Rye", "J.D. Salinger", "9780316769488", LocalDate.of(1951, 7, 16), "Fiction", true, null),
                new Book(null, "Moby Dick", "Herman Melville", "9781503280786", LocalDate.of(1851, 11, 14), "Adventure", true, null),
                new Book(null, "Brave New World", "Aldous Huxley", "9780060850524", LocalDate.of(1932, 8, 1), "Science Fiction", true, null),
                new Book(null, "Crime and Punishment", "Fyodor Dostoevsky", "9780143058144", LocalDate.of(1866, 1, 1), "Philosophical", true, null),
                new Book(null, "The Alchemist", "Paulo Coelho", "9780061122415", LocalDate.of(1988, 4, 15), "Fable", true, null)
        ));

        circulationRepository.deleteAll();

        User patron = userRepository.findByUsername("username")
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Circulation> circulations = new ArrayList<>();

        circulations.add(new Circulation(null, patron, books.get(0), LocalDate.now().minusDays(2), LocalDate.now().plusDays(10), null));
        books.get(0).setIsAvailable(false);

        circulations.add(new Circulation(null, patron, books.get(1), LocalDate.now().minusDays(20), LocalDate.now().minusDays(10), LocalDate.now().minusDays(5)));
        circulations.add(new Circulation(null, patron, books.get(2), LocalDate.now().minusDays(15), LocalDate.now().minusDays(5), LocalDate.now().minusDays(2)));
        circulations.add(new Circulation(null, patron, books.get(3), LocalDate.now().minusDays(18), LocalDate.now().minusDays(3), LocalDate.now().minusDays(1)));

        circulations.add(new Circulation(null, patron, books.get(4), LocalDate.now().minusDays(10), LocalDate.now().minusDays(2), LocalDate.now().minusDays(1)));
        circulations.add(new Circulation(null, patron, books.get(5), LocalDate.now().minusDays(12), LocalDate.now().minusDays(4), LocalDate.now().minusDays(3)));
        circulations.add(new Circulation(null, patron, books.get(6), LocalDate.now().minusDays(8), LocalDate.now().minusDays(1), LocalDate.now()));
        circulations.add(new Circulation(null, patron, books.get(7), LocalDate.now().minusDays(30), LocalDate.now().minusDays(10), LocalDate.now().minusDays(9)));
        circulations.add(new Circulation(null, patron, books.get(8), LocalDate.now().minusDays(14), LocalDate.now().minusDays(4), LocalDate.now().minusDays(2)));
        circulations.add(new Circulation(null, patron, books.get(9), LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), LocalDate.now()));

        bookRepository.saveAll(books);
        circulationRepository.saveAll(circulations);


    }
}
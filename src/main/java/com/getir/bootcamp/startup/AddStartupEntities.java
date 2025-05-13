package com.getir.bootcamp.startup;

import com.getir.bootcamp.entity.Role;
import com.getir.bootcamp.entity.User;
import com.getir.bootcamp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddStartupEntities implements ApplicationRunner {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(ApplicationArguments args) throws Exception {
//        userRepository.deleteAll();
        if (!userRepository.existsByUsername("librarian")) {
            userRepository.save(new User("librarian", passwordEncoder.encode("password"), "librarian", "librarian", true, Role.ROLE_LIBRARIAN, null));
        }
        if (!userRepository.existsByUsername("patron")) {
            userRepository.save(new User("patron", passwordEncoder.encode("password"), "patron", "patron", true, Role.ROLE_PATRON, null));
        }
    }
}
package com.getir.bootcamp.controller;

import com.getir.bootcamp.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {


    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public User getUSer() {
        return null;
    }
}

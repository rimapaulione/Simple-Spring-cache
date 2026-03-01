package com.example.caching.controller;

import com.example.caching.model.User;
import com.example.caching.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User create(@RequestBody final User user) {
        return userService.create(user.getName(), user.getLastName());
    }
    @GetMapping("/{id}")
    public User getById(@PathVariable final Long id){
        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable final Long id){
        userService.deleteById(id);
    }

}

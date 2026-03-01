package com.example.caching.service;

import com.example.caching.model.User;
import com.example.caching.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clear() {
        UserCache.clear();
    }

    @Test
    void test_shouldReturnUserFromCache() {
        User user = new User(1L, "Jonas", "Jonaitis");
        UserCache.put(1L, user);

        User result = userService.getUser(1L);

        verifyNoInteractions(userRepository);
        assertEquals("Jonas", result.getName());
    }

    @Test
    void test_shouldReturnUserFromDB() {
        User user = new User(1L, "Jonas", "Jonaitis");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertEquals("Jonas", result.getName());
        verify(userRepository).findById(1L);
        assertNotNull(UserCache.get(1L));
    }

    @Test
    void test_shouldThrowException() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getUser(1L));

        verify(userRepository).findById(1L);
    }

}
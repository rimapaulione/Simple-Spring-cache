package com.example.caching.service;

import com.example.caching.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;


class UserCacheTest {

    @AfterEach
    void cleanup() {
        UserCache.clear();
    }

    @Test
    void test_shouldAddAndReturnUser() {
        User user = new User(1L, "Jonas", "Jonaitis");
        UserCache.put(1L, user);

        User result = UserCache.get(1L);

        assertNotNull(result);
        assertEquals("Jonas", result.getName());
    }

    @Test
    void test_shouldReturnNullForMissingKey() {
        User user = new User(1L, "Jonas", "Jonaitis");
        UserCache.put(1L, user);

        assertNull(UserCache.get(9999L));
    }

    @Test
    void test_shouldReturnMapOfUsers() {
        User user1 = new User(1L, "Jonas", "Jonaitis");
        User user2 = new User(2L, "Tomas", "Tomaitis");
        UserCache.put(1L, user1);
        UserCache.put(2L, user2);


        Map<Long, User> result = UserCache.getAll();

        assertNull(result.get(1L));
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tomas", result.get(2L).getName());
    }

    @Test
    void test_shouldEvictUserById() {
        User user1 = new User(1L, "Jonas", "Jonaitis");
        UserCache.put(1L, user1);

        UserCache.evict(1L);

        assertNull(UserCache.get(1L));
    }

    @Test
    void test_shouldClearAllUsers() {
        UserCache.put(1L, new User(1L, "Jonas", "Jonaitis"));
        UserCache.put(2L, new User(2L, "Tomas", "Tomaitis"));

        UserCache.clear();

        assertEquals(0, UserCache.getAll().size());

    }
}
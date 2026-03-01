package com.example.caching.service;


import com.example.caching.model.User;
import com.example.caching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User create(final String name, final String lastName) {
        User user = userRepository.save(new User(name, lastName));
        UserCache.put(user.getId(), user);
        return user;
    }

    @Transactional
    public User getUser(final Long id) {
        User userFromCache = UserCache.get(id);
        if (userFromCache != null) return userFromCache;

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User is not found"));
        UserCache.put(user.getId(), user);
        return user;
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        UserCache.evict(id);
    }

    @Transactional
    public void delete() {
        userRepository.deleteAll();
        UserCache.clear();
    }
}

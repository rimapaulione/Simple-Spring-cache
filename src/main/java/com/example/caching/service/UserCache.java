package com.example.caching.service;

import com.example.caching.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserCache {
    private static final Map<Long, User> CACHE = new ConcurrentHashMap<>();



    public static void put(final Long id,final User user) {
        CACHE.put(id, user);
    }

    public static User get(final Long id) {
        return CACHE.get(id);
    }

    public static Map<Long, User> getAll() {
        return Map.copyOf(CACHE);
    }

    public static void evict(final Long id){
        CACHE.remove(id);
    }

    public static  void clear() {
        CACHE.clear();
    }
}

package com.example.caching.service;

import com.example.caching.model.Country;
import com.example.caching.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {

    private final CountryRepository repository;

    @CacheEvict(value = "countries", allEntries = true)
    public Country create(String name) {
        Country country = Country.builder().title(name).build();
        return repository.save(country);
    }

    @Cacheable(value = "countries")
    public List<Country> getAll() {
        log.info("I am getting from db");
        this.sleep();
        return repository.findAll();
    }

    @Cacheable(value = "countryById", key = "#id")
    public Country getById(Long id) {
        log.info("Getting country {} from DB", id);
        this.sleep();
        return repository.getById(id);
    }

    @CachePut(value = "countryById", key = "#id")
    @CacheEvict(value = "countries", allEntries = true)
    public Country update(Long id, String newName) {
        Country country = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Country not found: " + id));

        country.setTitle(newName);
        return repository.save(country);
    }


    public void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread was interrupted", e);
        }
    }
}

package com.example.caching.service;

import com.example.caching.dto.ResponseCountryDTO;
import com.example.caching.model.Country;
import com.example.caching.repository.CountryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CountryService {

    private final CountryRepository countryRepository;

@CacheEvict(value="countries", allEntries = true)
    public Country create(String title) {
        Country country = Country.builder().title(title).build();

        return countryRepository.save(country);
    }

    @Cacheable(value="countries")
    public List<Country> getAll() throws InterruptedException {
        log.info("I am calling service");
        Thread.sleep(5000);
        return countryRepository.findAll();

    }


}

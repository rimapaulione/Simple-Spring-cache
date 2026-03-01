package com.example.caching.controller;


import com.example.caching.dto.RequestCountryDTO;

import com.example.caching.model.Country;
import com.example.caching.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryService countryService;

    @PostMapping
    public ResponseEntity<Country> create(@RequestBody RequestCountryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(countryService.create(dto.title()));
    }

    @GetMapping
    public ResponseEntity<List<Country>> getAll() {
        return ResponseEntity.ok().body(countryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Country> getById(@PathVariable Long id) {
        return ResponseEntity.ok().body(countryService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Country> update(@PathVariable Long id, @RequestBody RequestCountryDTO newName) {
        return ResponseEntity.ok().body(countryService.update(id, newName.title()));
    }
}



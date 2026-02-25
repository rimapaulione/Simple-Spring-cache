package com.example.caching.controller;


import com.example.caching.dto.RequestCountryDTO;
import com.example.caching.dto.ResponseCountryDTO;
import com.example.caching.model.Country;
import com.example.caching.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryService service;

    @PostMapping
    public ResponseEntity<Country> create(@RequestBody RequestCountryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto.title()));
    }

    @GetMapping
    public ResponseEntity<List<Country>> getAll() throws InterruptedException {
        return ResponseEntity.ok().body(service.getAll());
    }
}

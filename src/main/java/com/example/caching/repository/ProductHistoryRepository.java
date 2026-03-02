package com.example.caching.repository;

import com.example.caching.model.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {
}

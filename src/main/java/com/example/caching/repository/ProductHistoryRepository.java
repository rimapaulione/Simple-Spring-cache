package com.example.caching.repository;

import com.example.caching.model.ProductHistory;
import com.example.caching.model.PurchaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {
    List<ProductHistory> findByStatus(PurchaseStatus status);

    List<ProductHistory> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}


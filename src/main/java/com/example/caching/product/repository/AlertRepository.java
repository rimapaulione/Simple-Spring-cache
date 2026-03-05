package com.example.caching.product.repository;


import com.example.caching.product.model.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<StockAlert, Long> {
}

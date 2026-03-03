package com.example.caching.service;

import com.example.caching.event.ProductEvent;
import com.example.caching.event.StockEvent;
import com.example.caching.model.Product;
import com.example.caching.model.PurchaseStatus;
import com.example.caching.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher publisher;
    private final ProductCache productCache;
    private final int MIN_STOCK = 5;

    @Transactional
    public Product create(final String name, final Double price, final Integer quantity) {
        Product product = productRepository.save(Product.builder()
                .name(name)
                .price(price)
                .quantity(quantity)
                .build());

        log.info("Product was created");
        productCache.put(product.getId(), product);
        publisher.publishEvent(new ProductEvent(product.getName(), PurchaseStatus.ADDED));

        if (product.getQuantity() < MIN_STOCK) {
            publisher.publishEvent(new StockEvent(product.getId(), product.getName(), product.getQuantity()));
        }

        return product;
    }

    public List<Product> getAll() {
        List<Product> cached = productCache.getAll();
        if (cached.isEmpty()) {
            return productRepository.findAll();
        }
        return cached;

    }

    public Product get(final Long id) {
        Product product = getProduct(id);
        productCache.put(product.getId(), product);
        return product;
    }

    @Transactional
    public Product updatePrice(final Long id, final Double newPrice) {

        if (newPrice == null || newPrice < 0) {
            throw new IllegalArgumentException("Price must be positive number");
        }

        Product product = getProduct(id);
        product.setPrice(newPrice);

        log.info("Product price is changed");
        publisher.publishEvent(new ProductEvent(product.getName(), PurchaseStatus.UPDATED));
        return saveProduct(product);
    }

    @Transactional
    public Product reduceQuantity(final Long id, final int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount have to be positive number");
        }

        Product product = getProduct(id);

        if (amount > product.getQuantity()) {
            throw new IllegalArgumentException("Amount is bigger then product quantity");
        }
        product.setQuantity(product.getQuantity() - amount);

        publisher.publishEvent(new ProductEvent(product.getName(), PurchaseStatus.PURCHASED));

        if (product.getQuantity() < MIN_STOCK) {
            publisher.publishEvent(new StockEvent(product.getId(), product.getName(), product.getQuantity()));
        }

        log.info("Product quantity is changed");

        return saveProduct(product);
    }

    @Transactional
    public void deleteProduct(final Long id) {

        Product product = getProduct(id);
        productRepository.deleteById(id);
        productCache.evict(id);


        publisher.publishEvent(new ProductEvent(product.getName(), PurchaseStatus.DELETED));
        log.info("Product was deleted");
    }

    private Product saveProduct(final Product product) {
        Product productFromDb = productRepository.save(product);
        productCache.put(productFromDb.getId(), productFromDb);
        return productFromDb;
    }

    private Product getProduct(final Long id) {
        Product product = productCache.get(id);

        if (product == null) {
            product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product is not found"));
        }
        return product;
    }
}

package com.example.caching.product.service;

import com.example.caching.product.dto.CreateProductRequest;
import com.example.caching.product.dto.ProductResponse;
import com.example.caching.product.dto.StatisticsResponse;
import com.example.caching.product.event.ProductEvent;
import com.example.caching.product.event.StockEvent;
import com.example.caching.product.model.Product;
import com.example.caching.product.model.PurchaseStatus;
import com.example.caching.product.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.caching.product.util.ProductConstants.MIN_STOCK;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher publisher;
    private final ProductCache productCache;


    public ProductResponse create(final CreateProductRequest createProductRequest) {
        Product product = this.saveProduct(Product.builder()
                .name(createProductRequest.name())
                .price(createProductRequest.price())
                .quantity(createProductRequest.quantity())
                .build());

        log.info("Product is created: {}", product.getId());

        this.callEvents(product, PurchaseStatus.ADDED);

        return ProductResponse.from(product);
    }

    public List<ProductResponse> getAll(String name, String stockStatus) {
        List<Product> products;

        products = productCache.getAll();
        if (products.isEmpty()) {
            products = productRepository.findAll();
        }
        if (name != null) {
            products = products.stream().filter(p -> p.getName().toLowerCase().contains(name.toLowerCase())).toList();
        }

        List<ProductResponse> responses = products.stream().map(ProductResponse::from).toList();

        if (stockStatus != null) {
            responses = responses.stream().filter(r -> r.stockStatus().equalsIgnoreCase(stockStatus)).toList();
        }
        return responses;
    }

    public ProductResponse get(final Long id) {
        return ProductResponse.from(getProduct(id));
    }

    public StatisticsResponse getStatistics() {
        List<Product> products = productRepository.findAll();

        long totalCount = products.size();
        double inventoryValue = products.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();

        long lowStockCount = products.stream()
                .filter(p -> p.getQuantity() > 0 && p.getQuantity() < MIN_STOCK)
                .count();

        long outOfStockCount = products.stream()
                .filter(p -> p.getQuantity() == 0)
                .count();


        return new StatisticsResponse(totalCount, inventoryValue, lowStockCount, outOfStockCount);
    }

    @Transactional
    public ProductResponse updateName(Long id, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
        Product product = getProduct(id);
        product.setName(newName);

        Product savedProduct = this.saveProduct(product);

        log.info("Product name updated for id: {}", id);
        this.callEvents(savedProduct, PurchaseStatus.UPDATED);
        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse updatePrice(final Long id, final Double newPrice) {

        if (newPrice == null || newPrice <= 0) {
            throw new IllegalArgumentException("Price must be positive number");
        }

        Product product = getProduct(id);
        product.setPrice(newPrice);

        Product savedProduct = this.saveProduct(product);

        log.info("Product price updated for id: {}", id);
        this.callEvents(savedProduct, PurchaseStatus.UPDATED);
        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse reduceQuantity(final Long id, final int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must to be positive number");
        }
        Product product = getProduct(id);

        if (amount > product.getQuantity()) {
            throw new IllegalArgumentException("Amount exceeds available quantity");
        }
        product.setQuantity(product.getQuantity() - amount);

        Product savedProduct = this.saveProduct(product);

        log.info("Product amount updated for id: {}", id);

        this.callEvents(savedProduct, PurchaseStatus.PURCHASED);

        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse extendQuantity(final Long id, final int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount have to be positive number");
        }
        Product product = getProduct(id);
        product.setQuantity(product.getQuantity() + amount);

        Product savedProduct = this.saveProduct(product);

        log.info("Product amount updated for id: {}", id);
        this.callEvents(savedProduct, PurchaseStatus.RESTOCKED);
        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public void deleteProduct(final Long id) {

        Product product = getProduct(id);
        productRepository.deleteById(id);
        productCache.evict(id);

        publisher.publishEvent(new ProductEvent(product.getId(), product.getName(), PurchaseStatus.DELETED));

        log.info("Product was deleted: {}", product.getId());
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
            productCache.put(product.getId(), product);
        }
        return product;
    }

    private void callEvents(Product product, PurchaseStatus status) {
        publisher.publishEvent(new ProductEvent(product.getId(), product.getName(), status));

        if (product.getQuantity() < MIN_STOCK) {
            publisher.publishEvent(new StockEvent(product.getId(), product.getName(), product.getQuantity()));
        }
    }


}

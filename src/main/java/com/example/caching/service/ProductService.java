package com.example.caching.service;


import com.example.caching.event.ProductEvent;
import com.example.caching.model.Product;
import com.example.caching.model.PurchaseStatus;
import com.example.caching.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Product create(final String name, final Double price, final Integer quantity) {
        Product product = productRepository.save(Product.builder()
                .name(name)
                .price(price)
                .quantity(quantity)
                .build());
        log.info("Product was created");
        ProductCache.put(product.getId(), product);


        publisher.publishEvent(new ProductEvent(product.getName(), PurchaseStatus.ADDED));

        return product;
    }

    public Product get(final Long id) {

        Product productFromCache = ProductCache.get(id);

        if (productFromCache != null) {
            log.info("Product returned form cache " + id);
            return productFromCache;
        }
        Product productFromDb = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product is not found"));
        log.info("Product return from DB " + id);
        ProductCache.put(productFromDb.getId(), productFromDb);
        return productFromDb;
    }

    @Transactional
    public Product updatePrice(final Long id, final Double newPrice) {

        if (newPrice == null || newPrice < 0) {
            throw new IllegalArgumentException("Price must be positive number");
        }

        Product product = getProduct(id);
        product.setPrice(newPrice);

        log.info("Product price is changed");
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

        log.info("Product quantity is changed");

        return saveProduct(product);
    }

    @Transactional
    public void deleteProduct(Long id) {

        Product product = getProduct(id);
        productRepository.deleteById(id);
        ProductCache.evict(id);


        publisher.publishEvent(new ProductEvent(product.getName(), PurchaseStatus.DELETED));
        log.info("Product was deleted");
    }

    private Product saveProduct(final Product product) {
        Product productFromDb = productRepository.save(product);
        ProductCache.put(productFromDb.getId(), productFromDb);
        return productFromDb;
    }

    private Product getProduct(final Long id) {
        Product product = ProductCache.get(id);

        if (product == null) {
            product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product is not found"));
        }
        return product;
    }

}

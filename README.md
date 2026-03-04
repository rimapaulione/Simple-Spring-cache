# Spring Boot Caching & Events - Learning Project

A learning project to explore different caching strategies and Spring event-driven architecture.

## Tech Stack

- Spring Boot 4.0.3
- Java 17
- H2 in-memory database
- Lombok

## Tasks

### 1. Country - Spring Cache Annotations

Simple caching using built-in Spring annotations (`@Cacheable`, `@CachePut`, `@CacheEvict`).

- `@Cacheable` - caches `getAll()` and `getById()` results, skips DB on cache hit
- `@CacheEvict` - invalidates cache on `create()` and `update()`
- `@CachePut` - updates cache entry on `update()`
- Includes `Thread.sleep(5000)` to demonstrate the caching speed difference

**Endpoints:** `/api/v1/countries`

### 2. User - Manual Caching with ConcurrentHashMap

Custom cache implementation using a static `ConcurrentHashMap`.

- `UserCache` - static utility class with `put`, `get`, `getAll`, `evict`, `clear`
- Service manually checks cache before querying DB
- Unit tests for `UserCache` and `UserService`

**Endpoints:** `/api/users`

### 3. Product, ProductHistory & StockAlert - Caching + Events

Combines `ConcurrentHashMap` caching with Spring's event system (`ApplicationEventPublisher` + `@TransactionalEventListener`).

**Caching:**
- `ProductCache` - Spring `@Component` with `ConcurrentHashMap`, injectable and testable
- Service checks cache first, falls back to DB

**Events:**
- `ProductEvent` - published on create, update, purchase, delete
- `StockEvent` - published when product quantity drops below threshold
- `@TransactionalEventListener(phase = AFTER_COMMIT)` - listeners fire only after successful transaction commit
- `@Transactional(propagation = REQUIRES_NEW)` - listeners run in their own transaction

**ProductHistory** - logs all product operations with timestamp and status (`ADDED`, `UPDATED`, `PURCHASED`, `DELETED`)

**StockAlert** - records low-stock warnings

**Endpoints:**
- `/products` - CRUD + purchase operations
- `/products/history` - view product history, filter by status or date range

## Tests

Unit tests using **JUnit 5** and **Mockito**.

- `UserCacheTest` - tests for static cache operations (put, get, getAll, evict, clear)
- `UserServiceTest` - tests with mocked repository, verifies cache hit/miss behavior
- `ProductCacheTest` - tests for `@Component` cache (put, get, getAll, evict, clear, overwrite)
- `ProductServiceTest` - tests with mocked repository, cache, and event publisher:
    - CRUD operations (create, get, update, delete)
    - Cache hit vs DB fallback
    - Event publishing verification (`ProductEvent`, `StockEvent`)
    - Validation (invalid price, negative amounts, exceeding quantity)
    - Exception handling (product not found)

```bash
./mvnw test
```

## Running

```bash
./mvnw spring-boot:run
```

H2 console available at: `http://localhost:8080/h2-console`

package com.inventoryapi.service;

import java.util.List;

import com.inventoryapi.dto.ProductRequest;
import com.inventoryapi.dto.ProductResponse;
import com.inventoryapi.entity.Product;
import com.inventoryapi.exception.ProductNotFoundException;
import com.inventoryapi.mapper.ProductMapper;
import com.inventoryapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();

        Product saved = repository.save(product);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = getProductOrThrow(id);
        return mapper.toResponse(product);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProductOrThrow(id);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        Product updated = repository.save(product);
        return mapper.toResponse(updated);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /**
     * Retourne les produits dont le stock est strictement inférieur au seuil
     * (5 unités par défaut, cf. ProductMapper.LOW_STOCK_THRESHOLD).
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> findLowStock(int threshold) {
        return repository.findByQuantityLessThan(threshold).stream()
                .map(mapper::toResponse)
                .toList();
    }

    private Product getProductOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}

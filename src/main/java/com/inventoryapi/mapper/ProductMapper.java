package com.inventoryapi.mapper;

import com.inventoryapi.dto.ProductResponse;
import com.inventoryapi.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    /**
     * Seuil en dessous duquel un produit est considéré en stock bas.
     */
    public static final int LOW_STOCK_THRESHOLD = 5;

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .lowStock(product.getQuantity() < LOW_STOCK_THRESHOLD)
                .build();
    }
}

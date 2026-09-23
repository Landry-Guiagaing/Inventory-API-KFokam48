package com.inventoryapi.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.inventoryapi.dto.ProductRequest;
import com.inventoryapi.dto.ProductResponse;
import com.inventoryapi.entity.Product;
import com.inventoryapi.exception.ProductNotFoundException;
import com.inventoryapi.mapper.ProductMapper;
import com.inventoryapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService service;

    @Test
    void create_devraitSauvegarderEtRetournerLeProduit() {
        ProductRequest request = ProductRequest.builder()
                .name("Clavier mécanique").price(new BigDecimal("49.99")).quantity(20).build();
        Product saved = Product.builder().id(1L).name("Clavier mécanique")
                .price(new BigDecimal("49.99")).quantity(20).build();
        ProductResponse response = ProductResponse.builder()
                .id(1L).name("Clavier mécanique").price(new BigDecimal("49.99")).quantity(20).lowStock(false).build();

        when(repository.save(any(Product.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ProductResponse result = service.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.isLowStock()).isFalse();
    }

    @Test
    void findById_devraitLeverProductNotFoundException_siInexistant() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_devraitLeverProductNotFoundException_siInexistant() {
        when(repository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(42L))
                .isInstanceOf(ProductNotFoundException.class);

        verify(repository, never()).deleteById(any());
    }

    @Test
    void findLowStock_devraitRetournerUniquementLesProduitsSousLeSeuil() {
        Product lowStockProduct = Product.builder().id(1L).name("Souris").price(new BigDecimal("15.00")).quantity(2).build();
        ProductResponse response = ProductResponse.builder()
                .id(1L).name("Souris").price(new BigDecimal("15.00")).quantity(2).lowStock(true).build();

        when(repository.findByQuantityLessThan(5)).thenReturn(List.of(lowStockProduct));
        when(mapper.toResponse(lowStockProduct)).thenReturn(response);

        List<ProductResponse> results = service.findLowStock(5);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).isLowStock()).isTrue();
    }

    @Test
    void update_devraitModifierPrixEtQuantite() {
        Product existing = Product.builder().id(1L).name("Écran").price(new BigDecimal("150.00")).quantity(10).build();
        ProductRequest request = ProductRequest.builder()
                .name("Écran").price(new BigDecimal("139.99")).quantity(3).build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toResponse(existing)).thenReturn(
                ProductResponse.builder().id(1L).name("Écran").price(new BigDecimal("139.99")).quantity(3).lowStock(true).build());

        ProductResponse result = service.update(1L, request);

        assertThat(existing.getQuantity()).isEqualTo(3);
        assertThat(result.isLowStock()).isTrue();
    }
}

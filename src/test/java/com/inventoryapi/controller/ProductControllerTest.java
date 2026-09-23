package com.inventoryapi.controller;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoryapi.dto.ProductRequest;
import com.inventoryapi.dto.ProductResponse;
import com.inventoryapi.exception.ProductNotFoundException;
import com.inventoryapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService service;

    @Test
    void create_avecRequeteValide_devraitRetourner201() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Clavier").price(new BigDecimal("49.99")).quantity(20).build();
        ProductResponse response = ProductResponse.builder()
                .id(1L).name("Clavier").price(new BigDecimal("49.99")).quantity(20).lowStock(false).build();

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Clavier"));
    }

    @Test
    void create_avecPrixNegatif_devraitRetourner400() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .name("Clavier").price(new BigDecimal("-10")).quantity(20).build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findById_produitInexistant_devraitRetourner404() throws Exception {
        when(service.findById(999L)).thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findLowStock_devraitRetournerLesProduitsEnStockBas() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(1L).name("Souris").price(new BigDecimal("15.00")).quantity(2).lowStock(true).build();

        when(service.findLowStock(anyInt())).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lowStock").value(true));
    }

    @Test
    void delete_devraitRetourner204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}

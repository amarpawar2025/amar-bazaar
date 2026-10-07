package com.amar.controller;

import com.amar.Service.ProductService;
import com.amar.exceptions.ProductException;
import com.amar.modal.Product;
import com.amar.response.ProductDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(
            @PathVariable Long productId)
            throws ProductException {

        Product product = productService.findProductById(productId);

        ProductDTO productDTO = ProductDTO.fromProduct(product);

        return new ResponseEntity<>(
                productDTO,
                HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam(required = false)
            String query) {

        List<Product> products =
                productService.searchProduct(query);

        List<ProductDTO> productDTOs =
                products.stream()
                        .map(ProductDTO::fromProduct)
                        .collect(Collectors.toList());

        return new ResponseEntity<>(
                productDTOs,
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String colors,
            @RequestParam(required = false) String sizes,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Integer minDiscount,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String stock,
            @RequestParam(required = false) Integer pageNumber) {

        Page<Product> products =
                productService.getAllProducts(
                        category,
                        brand,
                        colors,
                        sizes,
                        minPrice,
                        maxPrice,
                        minDiscount,
                        sort,
                        stock,
                        pageNumber);

        Page<ProductDTO> productDTOs =
                products.map(ProductDTO::fromProduct);

        return new ResponseEntity<>(
                productDTOs,
                HttpStatus.OK);
    }
}
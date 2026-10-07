package com.amar.Service;

import com.amar.modal.Product;
import com.amar.modal.Seller;
import com.amar.request.CreateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Product CreateProduct(
            CreateProductRequest req,
            Seller seller);

    void deleteProduct(Long productId);

    Product updateProduct(
            Long productId,
            Product product);

    Product findProductById(
            long productId);

    List<Product> findAllProducts();

    Page<Product> getAllProducts(
            String category,
            String brand,
            String colors,
            String sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber);

    List<Product> getProductBySeller(
            Long sellerId);

    List<Product> searchProduct(
            String query);
}
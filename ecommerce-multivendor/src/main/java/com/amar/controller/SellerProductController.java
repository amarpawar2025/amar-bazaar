package com.amar.controller;
import com.amar.Service.ProductService;
import com.amar.Service.SellerService;
import com.amar.exceptions.ProductException;
import com.amar.modal.Product;
import com.amar.modal.Seller;
import com.amar.request.CreateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers/products")
public class SellerProductController {

    private final ProductService productService;
    private final SellerService sellerService;

    @GetMapping()
    public ResponseEntity<List<Product>> getProductsId(
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        Seller seller =
                sellerService.getSellerProfile(jwt);

        List<Product> products =
                productService.getProductBySeller(
                        seller.getId());

        return new ResponseEntity<>(
                products,
                HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Product> createProducts(
            @RequestBody CreateProductRequest request,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        Seller seller =
                sellerService.getSellerProfile(jwt);

        Product product =
                productService.CreateProduct(
                        request,
                        seller);

        return new ResponseEntity<>(
                product,
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long productId) {

        try {

            productService.deleteProduct(productId);

            return new ResponseEntity<>(
                    HttpStatus.OK);

        } catch (Exception e) {

            return new ResponseEntity<>(
                    HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product product) {

        try {

            Product updatedProduct = productService.updateProduct(productId, product);

            return new ResponseEntity<>(
                    updatedProduct,
                    HttpStatus.OK);

        } catch (ProductException e) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
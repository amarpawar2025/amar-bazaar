package com.amar.Service.impl;

import com.amar.Service.ProductService;
import com.amar.exceptions.ProductException;
import com.amar.modal.Category;
import com.amar.modal.Product;
import com.amar.modal.Seller;
import com.amar.repository.CategoryRepository;
import com.amar.repository.ProductRepository;
import com.amar.request.CreateProductRequest;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // ================= CREATE PRODUCT =================

    @Override
    public Product CreateProduct(
            CreateProductRequest req,
            Seller seller) {

        System.out.println("========== CREATE PRODUCT ==========");
        System.out.println("Images received from frontend: " + req.getImages());

        Category category1 =
                categoryRepository.findByCategoryId(
                        req.getCategory());

        if (category1 == null) {

            Category category = new Category();

            category.setCategoryId(
                    req.getCategory());

            category.setLevel(1);

            category1 =
                    categoryRepository.save(category);
        }

        Category category2 =
                categoryRepository.findByCategoryId(
                        req.getCategory2());

        if (category2 == null) {

            Category category = new Category();

            category.setCategoryId(
                    req.getCategory2());

            category.setLevel(2);

            category.setParentCategory(
                    category1);

            category2 =
                    categoryRepository.save(category);
        }

        Category category3 =
                categoryRepository.findByCategoryId(
                        req.getCategory3());

        if (category3 == null) {

            Category category = new Category();

            category.setCategoryId(
                    req.getCategory3());

            category.setLevel(3);

            category.setParentCategory(
                    category2);

            category3 =
                    categoryRepository.save(category);
        }

        int discountPercentage =
                calculateDiscountPercentage(
                        req.getMrpPrice(),
                        req.getSellingPrice());

        Product product = new Product();

        product.setSeller(seller);

        product.setCategory(category3);

        product.setDescription(
                req.getDescription());

        product.setCreatedAt(
                LocalDateTime.now());

        product.setTitle(
                req.getTitle());

        product.setColor(
                req.getColor());

        product.setSellingPrice(
                req.getSellingPrice());

        product.setMrPrice(
                req.getMrpPrice());

        product.setSizes(
                req.getSizes());

        // STOCK QUANTITY
        product.setQuantity(
                req.getQuantity());

        // PRODUCT IMAGES
        product.setImages(
                req.getImages());

        System.out.println(
                "Images set in product: "
                        + product.getImages());

        product.setDiscountPercent(
                discountPercentage);

        Product savedProduct =
                productRepository.save(product);

        System.out.println(
                "Product saved successfully. ID: "
                        + savedProduct.getId());

        System.out.println(
                "Saved product images: "
                        + savedProduct.getImages());

        System.out.println(
                "===================================");

        return savedProduct;
    }

    // ================= DISCOUNT =================

    private int calculateDiscountPercentage(
            int mrPrice,
            int sellingPrice) {

        if (mrPrice <= 0) {

            throw new IllegalArgumentException(
                    "Actual price must be greater than 0");
        }

        double discount =
                mrPrice - sellingPrice;

        double discountPercentage =
                (discount / mrPrice) * 100;

        return (int) discountPercentage;
    }

    // ================= DELETE PRODUCT =================

    @Override
    public void deleteProduct(
            Long productId) {

        Product product =
                findProductById(productId);

        productRepository.delete(product);
    }

    // ================= UPDATE PRODUCT =================

    @Override
    public Product updateProduct(
            Long productId,
            Product product) {

        Product existingProduct =
                findProductById(productId);

        product.setId(
                existingProduct.getId());

        return productRepository.save(product);
    }

    // ================= FIND PRODUCT BY ID =================

    @Override
    public Product findProductById(
            long productId) {

        return productRepository
                .findById(productId)
                .orElseThrow(() ->
                        new ProductException(
                                "Product not found with id "
                                        + productId));
    }

    // ================= FIND ALL PRODUCTS =================

    @Override
    public List<Product> findAllProducts() {

        return productRepository.findAll();
    }

    // ================= GET ALL PRODUCTS =================

    @Override
    public Page<Product> getAllProducts(
            String category,
            String brand,
            String colors,
            String sizes,
            Integer minPrice,
            Integer maxPrice,
            Integer minDiscount,
            String sort,
            String stock,
            Integer pageNumber) {

        Specification<Product> spec =
                (root, query, criteriaBuilder) -> {

                    List<Predicate> predicates =
                            new ArrayList<>();

                    // ================= CATEGORY FILTER =================

                    if (category != null &&
                            !category.isEmpty()) {

                        Category selectedCategory =
                                categoryRepository.findByCategoryId(
                                        category);

                        if (selectedCategory != null) {

                            List<String> categoryIds =
                                    new ArrayList<>();

                            collectCategoryIds(
                                    selectedCategory,
                                    categoryIds);

                            Join<Product, Category>
                                    categoryJoin =
                                    root.join("category");

                            predicates.add(
                                    categoryJoin
                                            .get("categoryId")
                                            .in(categoryIds)
                            );

                        } else {

                            // Category does not exist
                            predicates.add(
                                    criteriaBuilder.disjunction()
                            );
                        }
                    }

                    // ================= BRAND FILTER =================

                    if (brand != null &&
                            !brand.isEmpty()) {

                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get("brand"),
                                        brand
                                )
                        );
                    }

                    // ================= COLOR FILTER =================

                    if (colors != null &&
                            !colors.isEmpty()) {

                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get("color"),
                                        colors
                                )
                        );
                    }

                    // ================= SIZE FILTER =================

                    if (sizes != null &&
                            !sizes.isEmpty()) {

                        predicates.add(
                                criteriaBuilder.equal(
                                        root.get("Sizes"),
                                        sizes
                                )
                        );
                    }

                    // ================= MINIMUM PRICE =================

                    if (minPrice != null) {

                        predicates.add(
                                criteriaBuilder
                                        .greaterThanOrEqualTo(
                                                root.get(
                                                        "sellingPrice"),
                                                minPrice
                                        )
                        );
                    }

                    // ================= MAXIMUM PRICE =================

                    if (maxPrice != null) {

                        predicates.add(
                                criteriaBuilder
                                        .lessThanOrEqualTo(
                                                root.get(
                                                        "sellingPrice"),
                                                maxPrice
                                        )
                        );
                    }

                    // ================= MINIMUM DISCOUNT =================

                    if (minDiscount != null) {

                        predicates.add(
                                criteriaBuilder
                                        .greaterThanOrEqualTo(
                                                root.get(
                                                        "discountPercent"),
                                                minDiscount
                                        )
                        );
                    }

                    // ================= STOCK FILTER =================

                    if (stock != null &&
                            !stock.isEmpty()) {

                        if (stock.equalsIgnoreCase(
                                "in_stock")) {

                            predicates.add(
                                    criteriaBuilder.greaterThan(
                                            root.get(
                                                    "quantity"),
                                            0
                                    )
                            );
                        }

                        if (stock.equalsIgnoreCase(
                                "out_of_stock")) {

                            predicates.add(
                                    criteriaBuilder.equal(
                                            root.get(
                                                    "quantity"),
                                            0
                                    )
                            );
                        }
                    }

                    return criteriaBuilder.and(
                            predicates.toArray(
                                    new Predicate[0]
                            )
                    );
                };

        // ================= PAGINATION =================

        int page =
                pageNumber == null
                        ? 0
                        : pageNumber;

        Pageable pageable;

        // ================= SORTING =================

        if (sort != null &&
                !sort.isEmpty()) {

            switch (sort) {

                case "price_low":

                    pageable =
                            PageRequest.of(
                                    page,
                                    10,
                                    Sort.by(
                                            Sort.Direction.ASC,
                                            "sellingPrice"
                                    )
                            );

                    break;

                case "price_high":

                    pageable =
                            PageRequest.of(
                                    page,
                                    10,
                                    Sort.by(
                                            Sort.Direction.DESC,
                                            "sellingPrice"
                                    )
                            );

                    break;

                case "newest":

                    pageable =
                            PageRequest.of(
                                    page,
                                    10,
                                    Sort.by(
                                            Sort.Direction.DESC,
                                            "createdAt"
                                    )
                            );

                    break;

                case "oldest":

                    pageable =
                            PageRequest.of(
                                    page,
                                    10,
                                    Sort.by(
                                            Sort.Direction.ASC,
                                            "createdAt"
                                    )
                            );

                    break;

                default:

                    pageable =
                            PageRequest.of(
                                    page,
                                    10
                            );

                    break;
            }

        } else {

            pageable =
                    PageRequest.of(
                            page,
                            10
                    );
        }

        return productRepository.findAll(
                spec,
                pageable
        );
    }

    // ================= CATEGORY HIERARCHY =================

    private void collectCategoryIds(
            Category category,
            List<String> categoryIds) {

        if (category == null) {
            return;
        }

        categoryIds.add(
                category.getCategoryId()
        );

        List<Category> children =
                categoryRepository.findByParentCategory(
                        category
                );

        for (Category child : children) {

            collectCategoryIds(
                    child,
                    categoryIds
            );
        }
    }

    // ================= SELLER PRODUCTS =================

    @Override
    public List<Product> getProductBySeller(
            Long sellerId) {

        return productRepository.findBySellerId(
                sellerId
        );
    }

    // ================= SEARCH PRODUCT =================

    @Override
    public List<Product> searchProduct(
            String query) {

        return productRepository.searchProductBy(
                query
        );
    }
}
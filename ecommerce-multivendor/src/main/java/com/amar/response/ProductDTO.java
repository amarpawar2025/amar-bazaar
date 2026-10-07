package com.amar.response;

import com.amar.modal.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    private String title;

    private String description;

    private int mrPrice;

    private int sellingPrice;

    private int discountPercent;

    private int quantity;

    private String color;

    private List<String> images;

    private int numRatings;

    private CategoryDTO category;

    private SellerDTO seller;

    private LocalDateTime createdAt;

    private String Sizes;


    public static ProductDTO fromProduct(Product product) {

        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();

        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setDescription(product.getDescription());

        dto.setMrPrice(product.getMrPrice());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setDiscountPercent(product.getDiscountPercent());

        dto.setQuantity(product.getQuantity());
        dto.setColor(product.getColor());

        dto.setImages(product.getImages());

        dto.setNumRatings(product.getNumRatings());

        dto.setCreatedAt(product.getCreatedAt());

        dto.setSizes(product.getSizes());


        // Category
        if (product.getCategory() != null) {

            CategoryDTO categoryDTO = new CategoryDTO();

            categoryDTO.setId(product.getCategory().getId());
            categoryDTO.setName(product.getCategory().getName());
            categoryDTO.setCategoryId(
                    product.getCategory().getCategoryId()
            );
            categoryDTO.setLevel(
                    product.getCategory().getLevel()
            );

            dto.setCategory(categoryDTO);
        }


        // Seller
        if (product.getSeller() != null) {

            SellerDTO sellerDTO = new SellerDTO();

            sellerDTO.setId(product.getSeller().getId());
            sellerDTO.setSellerName(
                    product.getSeller().getSellerName()
            );

            dto.setSeller(sellerDTO);
        }

        return dto;
    }


    // =========================
    // CATEGORY DTO
    // =========================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDTO {

        private Long id;

        private String name;

        private String categoryId;

        private Integer level;
    }


    // =========================
    // SELLER DTO
    // =========================

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SellerDTO {

        private Long id;

        private String sellerName;
    }
}
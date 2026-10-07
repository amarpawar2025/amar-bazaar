package com.amar.controller;

import com.amar.Service.ProductService;
import com.amar.Service.ReviewService;
import com.amar.Service.UserService;
import com.amar.modal.Product;
import com.amar.modal.Review;
import com.amar.modal.User;
import com.amar.request.CreateReviewRequest;
import com.amar.response.ApiResponse;
import com.amar.response.ReviewDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;

    // =========================================================
    // GET ALL REVIEWS FOR PRODUCT
    // =========================================================

    @GetMapping("/products/{productId}/review")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(
            @PathVariable Long productId) {

        System.out.println(
                "GET REVIEWS CONTROLLER CALLED - PRODUCT ID: "
                        + productId
        );

        List<Review> reviews =
                reviewService.getReviewByProductId(productId);

        System.out.println(
                "REVIEWS FOUND: "
                        + reviews.size()
        );

        List<ReviewDTO> reviewDTOs =
                reviews.stream()
                        .map(this::convertToDTO)
                        .toList();

        System.out.println(
                "REVIEW DTO COUNT: "
                        + reviewDTOs.size()
        );

        return ResponseEntity.ok(reviewDTOs);
    }

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    @PostMapping("/products/{productId}/review")
    public ResponseEntity<ReviewDTO> writeReview(
            @RequestBody CreateReviewRequest req,
            @PathVariable Long productId,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        Product product =
                productService.findProductById(productId);

        Review review =
                reviewService.createReview(
                        req,
                        user,
                        product
                );

        return ResponseEntity.ok(
                convertToDTO(review)
        );
    }

    // =========================================================
    // UPDATE REVIEW
    // =========================================================

    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @RequestBody CreateReviewRequest req,
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        Review review =
                reviewService.updateReview(
                        reviewId,
                        req.getReviewText(),
                        req.getReviewRating(),
                        user.getId()
                );

        return ResponseEntity.ok(
                convertToDTO(review)
        );
    }

    // =========================================================
    // DELETE REVIEW
    // =========================================================

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String jwt)
            throws Exception {

        User user =
                userService.findUserByJwtToken(jwt);

        reviewService.deletereview(
                reviewId,
                user.getId()
        );

        ApiResponse response =
                new ApiResponse();

        response.setMessage(
                "Review deleted successfully"
        );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // CONVERT REVIEW TO DTO
    // =========================================================

    private ReviewDTO convertToDTO(
            Review review) {

        Long userId = null;

        String userName = "Customer";

        if (review.getUser() != null) {

            userId =
                    review.getUser().getId();

            if (review.getUser().getFullName() != null
                    && !review.getUser()
                    .getFullName()
                    .trim()
                    .isEmpty()) {

                userName =
                        review.getUser()
                                .getFullName();

            } else if (
                    review.getUser().getEmail() != null
                            && !review.getUser()
                            .getEmail()
                            .trim()
                            .isEmpty()) {

                userName =
                        review.getUser()
                                .getEmail();
            }
        }

        return new ReviewDTO(
                review.getId(),
                review.getReviewText(),
                review.getRating(),
                review.getProductImages(),
                review.getCreatedAt(),
                userId,
                userName
        );
    }
}
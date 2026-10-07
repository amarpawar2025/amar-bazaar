package com.amar.Service.impl;

import com.amar.Service.ReviewService;
import com.amar.modal.Product;
import com.amar.modal.Review;
import com.amar.modal.User;
import com.amar.repository.ReviewRepository;
import com.amar.request.CreateReviewRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review createReview(CreateReviewRequest req, User user, Product product) {

        Review review = new Review();

        review.setUser(user);
        review.setProduct(product);
        review.setReviewText(req.getReviewText());
        review.setRating(req.getReviewRating());
        review.setProductImages(req.getProductImages());

        product.getReviews().add(review);

        return reviewRepository.save(review);
    }

    @Override
    public List<Review> getReviewByProductId(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    @Override
    public Review updateReview(
            Long reviewId,
            String reviewText,
            double rating,
            Long userId) throws Exception {

        Review review = getReviewById(reviewId);

        if (review.getUser().getId().equals(userId)) {
            review.setReviewText(reviewText);
            review.setRating(rating);

            return reviewRepository.save(review);
        }

        throw new Exception("You can't update this review");
    }

    @Override
    public void deletereview(Long reviewId, Long userId) throws Exception {

        Review review = getReviewById(reviewId);

        if (!review.getUser().getId().equals(userId)) {
            throw new Exception("You can't delete this review");
        }

        reviewRepository.delete(review);
    }

    @Override
    public Review getReviewById(Long reviewId) throws Exception {

        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new Exception("Review not found"));
    }
}
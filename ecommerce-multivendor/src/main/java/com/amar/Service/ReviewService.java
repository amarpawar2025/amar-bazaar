package com.amar.Service;

import com.amar.modal.Product;
import com.amar.modal.Review;
import com.amar.modal.User;
import com.amar.request.CreateReviewRequest;

import java.util.List;

public interface ReviewService {

    Review createReview(CreateReviewRequest req,
                        User user,
                        Product product);



    List<Review> getReviewByProductId(Long productId);

    Review updateReview(Long reviewId,String reviewText,double rating,Long userId) throws Exception;


    void deletereview(Long reviewId,Long userId) throws Exception;

    Review getReviewById(Long reviewId) throws Exception;



}

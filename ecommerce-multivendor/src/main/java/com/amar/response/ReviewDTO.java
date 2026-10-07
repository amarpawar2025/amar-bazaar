package com.amar.response;

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
public class ReviewDTO {

    private Long id;

    private String reviewText;

    private double rating;

    private List<String> productImages;

    private LocalDateTime createdAt;

    private Long userId;

    private String userName;
}
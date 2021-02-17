package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Review;
import reactor.core.publisher.Flux;

import java.util.List;

public class ReviewService {

    public  List<Review> retrieveReviews(long MovieId){

        var reviewsList = List.of(new Review(MovieId, "Awesome Movie", 8.9),
                new Review(MovieId, "Excellent Movie", 9.0));
        return reviewsList;
    }

    public Flux<Review> retrieveReviewsFlux(long MovieId){

        var reviewsList = List.of(new Review(MovieId, "Awesome Movie", 8.9),
                new Review(MovieId, "Excellent Movie", 9.0));
        return Flux.fromIterable(reviewsList);
    }
}

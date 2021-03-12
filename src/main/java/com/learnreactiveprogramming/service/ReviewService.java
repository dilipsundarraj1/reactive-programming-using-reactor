package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Review;
import reactor.core.publisher.Flux;

import java.util.List;

public class ReviewService {

    public  List<Review> retrieveReviews(long movieInfoId){

        var reviewsList = List.of(new Review(movieInfoId, "Awesome Movie", 8.9),
                new Review(movieInfoId, "Excellent Movie", 9.0));
        return reviewsList;
    }

    public Flux<Review> retrieveReviewsFlux(long movieInfoId){

        var reviewsList = List.of(new Review(movieInfoId, "Awesome Movie", 8.9),
                new Review(movieInfoId, "Excellent Movie", 9.0));
        return Flux.fromIterable(reviewsList);
    }
}

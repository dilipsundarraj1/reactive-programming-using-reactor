package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();

    ReviewService reviewService= new ReviewService(webClient);

    @Test
    void retrieveAllReviews() {
        //given

        //when
        var reviewsFlux = reviewService.retrieveAllReviews();

        //then
        StepVerifier.create(reviewsFlux)
                .assertNext(review -> {
                    assertEquals("Nolan is the real superhero", review.getComment());
                })
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void retrieveReviewById() {
        //given
        Long reviewId = 1L;

        //when
        var reviewsFlux = reviewService.retrieveReviewById(reviewId);

        //then
        StepVerifier.create(reviewsFlux)
                .assertNext(review -> {
                    assertEquals("Nolan is the real superhero", review.getComment());
                })
                .verifyComplete();
    }
}
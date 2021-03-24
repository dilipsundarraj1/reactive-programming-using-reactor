package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovieReactiveServiceRestClientTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();
    MovieInfoService mis = new MovieInfoService(webClient);
    ReviewService rs = new ReviewService(webClient);
    RevenueService revenueService = new RevenueService();
    MovieReactiveService movieReactiveService = new MovieReactiveService(mis, rs,revenueService);

    @Test
    @Disabled
    void getAllMovies_RestClient() {
        //given

        //when
        var moviesFlux = movieReactiveService.getAllMovies_RestClient();

        //then
        StepVerifier.create(moviesFlux)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals("Nolan is the real superhero", movie.getReviewList().get(0).getComment());
                })
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    @Disabled
    void getMovieById_RestClient() {

        //given
        var movieInfoId = 1 ;

        //when
        var movieMono = movieReactiveService.getMovieById_RestClient(movieInfoId);

        //then
        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(movie.getReviewList().size(), 1);
                    //assertNotNull(movie.getRevenue());
                })
                .verifyComplete();
    }
}

package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;


class MovieReactiveServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();
    MovieInfoService mis = new MovieInfoService(webClient);
    ReviewService rs = new ReviewService(webClient);
    RevenueService revenueService = new RevenueService();
    MovieReactiveService movieReactiveService = new MovieReactiveService(mis, rs,revenueService);


    @Test
    void getAllMovieInfo() {
        //given

        //when
        var moviesInfo = movieReactiveService.getAllMovies();

        //then
        StepVerifier.create(moviesInfo)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);

                })
                .assertNext(movieInfo -> {
                    assertEquals("The Dark Knight", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .verifyComplete();

    }

    @Test
    void getAllMovieInfo_1() {

        //given

        //when
        var moviesInfo = movieReactiveService.getAllMovieInfo_1().log();

        //then
        StepVerifier.create(moviesInfo)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);

                })
                .assertNext(movieInfo -> {
                    assertEquals("The Dark Knight", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .verifyComplete();

    }

    @Test
    void getMovieById() {

        //given
        long movieId = 1L;

        //when
        Mono<Movie> movieMono = movieReactiveService.getMovieById(movieId);

        //then
        StepVerifier.create(movieMono)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .verifyComplete();
    }

    @Test
    void getMovieById_usingFlatMap() {

        //given
        long movieId = 1L;

        //when
        Mono<Movie> movieMono = movieReactiveService.getMovieById_usingFlatMap(movieId);

        //then
        StepVerifier.create(movieMono)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovieInfo().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .verifyComplete();
    }

    @Test
    void getMovieInfoById_withRevenue() {

        //given
        long movieId = 1L;

        //when
        Mono<Movie> movieMono = movieReactiveService.getMovieById_withRevenue(movieId);

        //then
        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(movie.getReviewList().size(), 2);
                })
                .verifyComplete();
    }

    @Test
    void getMovieInfoById_1() {

        //given
        long movieId = 1L;

        //when
        Mono<Movie> movieMono = movieReactiveService.getMovieById_1(movieId);

        //then
        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovieInfo().getName());
                    assertEquals(movie.getReviewList().size(), 2);
                    //assertNotNull(movie.getRevenue());
                })
                .verifyComplete();
    }



}
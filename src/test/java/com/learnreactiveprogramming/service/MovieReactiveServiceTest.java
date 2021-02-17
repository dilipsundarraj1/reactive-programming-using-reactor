package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.service.MovieInfoService;
import com.learnreactiveprogramming.service.MovieReactiveService;
import com.learnreactiveprogramming.service.RevenueService;
import com.learnreactiveprogramming.service.ReviewService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MovieReactiveServiceTest {

    MovieInfoService mis = new MovieInfoService();
    ReviewService rs = new ReviewService();
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
                    assertEquals("Batman Begins", movieInfo.getMovie().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);

                })
                .assertNext(movieInfo -> {
                    assertEquals("The Dark Knight", movieInfo.getMovie().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getMovie().getName());
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
                    assertEquals("Batman Begins", movieInfo.getMovie().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);

                })
                .assertNext(movieInfo -> {
                    assertEquals("The Dark Knight", movieInfo.getMovie().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .assertNext(movieInfo -> {
                    assertEquals("Dark Knight Rises", movieInfo.getMovie().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .verifyComplete();

    }

    @Test
    void getMovieById() {

        //given
        long movieId = 1L;

        //when
        Mono<Movie> movieMono = movieReactiveService.getMovieInfoById(movieId);

        //then
        StepVerifier.create(movieMono)
                .assertNext(movieInfo -> {
                    assertEquals("Batman Begins", movieInfo.getMovie().getName());
                    assertEquals(movieInfo.getReviewList().size(), 2);
                })
                .verifyComplete();
    }

    @Test
    void getMovieInfoById_withRevenue() {

        //given
        long movieId = 1L;

        //when
        Mono<Movie> movieMono = movieReactiveService.getMovieInfoById_withRevenue(movieId);

        //then
        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(movie.getReviewList().size(), 2);
                })
                .verifyComplete();
    }

    @Test
    void getMovieInfoById_1() {

        //given
        long movieId = 1L;

        //when
        Mono<Movie> movieMono = movieReactiveService.getMovieInfoById_1(movieId);

        //then
        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(movie.getReviewList().size(), 2);
                    //assertNotNull(movie.getRevenue());
                })
                .verifyComplete();
    }


}
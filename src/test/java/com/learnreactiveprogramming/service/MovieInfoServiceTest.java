package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080/movies")
            .build();
    MovieInfoService movieInfoService = new MovieInfoService(webClient);

    @Test
    void movieInfoFlux() {
        //given

        //when
        var movieInfoFlux = movieInfoService.retrieveAllMovieInfo();

        //then
        StepVerifier.create(movieInfoFlux)
                //.expectNextCount(7)
                .assertNext( movieInfo ->
                        assertEquals("Batman Begins", movieInfo.getName())

                )
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void retrieveMovieInfoById() {

        //given
        Long movieInfoId = 1L;

        //when
        var movieInfoFlux = movieInfoService.retrieveMovieInfoById(movieInfoId);

        //then
        StepVerifier.create(movieInfoFlux)
                //.expectNextCount(7)
                .assertNext( movieInfo ->
                        assertEquals("Batman Begins", movieInfo.getName())

                )
                .verifyComplete();
    }
}
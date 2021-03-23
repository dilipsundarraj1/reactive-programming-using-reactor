package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieReactiveServiceMockTest {

    @Mock
    MovieInfoService movieInfoService;

    @Mock
    ReviewService reviewService;

    @InjectMocks
    MovieReactiveService reactiveMovieService;

    @Test
    void getAllMovieInfo() {

        //given
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

        //when
        var movieFlux  =  reactiveMovieService.getAllMovies();

        //then
        StepVerifier.create(movieFlux)
                .expectNextCount(3)
                .verifyComplete();

    }


    @Test
    void getAllMovieInfo_error() {

        //given
        var errorMessage = "Exception Occurred in Review Service";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException(errorMessage));

        //when
        var movieFlux = reactiveMovieService.getAllMovies();

        //then
        StepVerifier.create(movieFlux)
                //.expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                /*.expectErrorSatisfies((ex)->{
                    var errorMsg = ex.getMessage();
                    assertEquals(errorMessage, errorMsg);

                })*/
                .verify();

    }

    @Test
    void getAllMovieInfo_error_retry() {

        //given
        var errorMessage = "Exception Occurred in Review Service";
        when(movieInfoService.retrieveMoviesFlux()).thenReturn(Flux.fromIterable(List.of(new MovieInfo(100l, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))));
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new RuntimeException(errorMessage));

        //when
        var movieFlux = reactiveMovieService.getAllMovies_retry();

        //then
        StepVerifier.create(movieFlux)
                //.expectError(MovieException.class)
                .expectErrorMessage(errorMessage)
                /*.expectErrorSatisfies((ex)->{
                    var errorMsg = ex.getMessage();
                    assertEquals(errorMessage, errorMsg);

                })*/
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));

    }

    @Test
    void getAllMovies_retry_when() {

        //given
        var errorMessage = "Exception Occurred in Review Service";
        when(movieInfoService.retrieveMoviesFlux()).thenReturn(Flux.fromIterable(List.of(new MovieInfo(100l, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))));
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new NetworkException(errorMessage));

        //when
        var movieFlux = reactiveMovieService.getAllMovies_retry_when().log();

        //then
        StepVerifier.create(movieFlux)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));

    }

    @Test
    void getAllMovies_retry_when_1() {

        //given
        var errorMessage = "Exception Occurred in Review Service";
        when(movieInfoService.retrieveMoviesFlux()).thenReturn(Flux.fromIterable(List.of(new MovieInfo(100l, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))));
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new ServiceException(errorMessage));

        //when
        var movieFlux = reactiveMovieService.getAllMovies_retry_when().log();

        //then
        StepVerifier.create(movieFlux)
                .expectErrorMessage(errorMessage)
                .verify();

        verify(reviewService, times(1)).retrieveReviewsFlux(isA(Long.class));

    }

    @Test
    void getAllMovies_repeat() {

        //given
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

        //when
        var movieFlux = reactiveMovieService.getAllMovies_repeat();

        //then
        StepVerifier.create(movieFlux)
                .expectNextCount(6)
                .thenCancel()
                .verify();


        verify(reviewService, times(6)).retrieveReviewsFlux(isA(Long.class));

    }

    @Test
    void getAllMovies_repeatN() {

        //given
        long n = 2;
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

        //when
        var movieFlux = reactiveMovieService.getAllMovies_repeatN(n);

        //then
        StepVerifier.create(movieFlux)
                .expectNextCount(9)
                .verifyComplete();


        verify(reviewService, times(9)).retrieveReviewsFlux(isA(Long.class));

    }

    @Test
    void getAllMovies_repeat_Exception() {

        //given
        var errorMessage = "Exception Occurred in Review Service";
        when(movieInfoService.retrieveMoviesFlux()).thenReturn(Flux.fromIterable(List.of(new MovieInfo(100l, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))));
        when(reviewService.retrieveReviewsFlux(anyLong())).thenThrow(new NetworkException(errorMessage));


        //when
        var movieFlux = reactiveMovieService.getAllMovies_repeat();

        //then
        StepVerifier.create(movieFlux)
                .expectErrorMessage(errorMessage)
                .verify();


        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));

    }

    @Test
    void getAllMovies_repeatWhen() {

        //given
        var errorMessage = "Exception Occurred in Review Service";
        when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();

        //when
        var movieFlux = reactiveMovieService.getAllMovies_repeatWhen();

        //then
        StepVerifier.create(movieFlux)
                .expectNextCount(6)
                .verifyComplete();


        verify(reviewService, times(6)).retrieveReviewsFlux(isA(Long.class));

    }
}

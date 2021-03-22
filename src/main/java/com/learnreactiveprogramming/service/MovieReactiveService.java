package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.NetworkException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class MovieReactiveService {

    private MovieInfoService movieInfoService;
    private ReviewService reviewService;
    private RevenueService revenueService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies() {

        var movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        var movies = movieInfoFlux
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono =
                            reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                                    .collectList();
                    return reviewsMono
                            .map(reviewList -> new Movie(movieInfo, reviewList));
                }))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    ;
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                });

        return movies;
    }

    public Flux<Movie> getAllMovies_RestClient() {

        var movieInfoFlux = movieInfoService.retrieveAllMovieInfo_RestClient();

        var movies = movieInfoFlux
                //.flatMap((movieInfo -> {
                .flatMapSequential((movieInfo -> {
                    Mono<List<Review>> reviewsMono =
                            reviewService.retrieveReviewsFlux_RestClient(movieInfo.getMovieInfoId())
                                    .collectList();
                    return reviewsMono
                            .map(movieList -> new Movie(movieInfo, movieList));
                }))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    ;
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                });

        return movies;
    }

    public Flux<Movie> getAllMovies_retry() {

        var movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        var movies = movieInfoFlux
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(movieList -> new Movie(movieInfo, movieList));
                }))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    ;
                    throw new MovieException(ex.getMessage());
                })
                //.retry();
                .retry(3);

        return movies;
    }


    public static Retry retrySpec() {

        //  Retry with a back of 500 ms everytime
        //  return Retry.backoff(3, Duration.ofMillis(500));

        return Retry.fixedDelay(3, Duration.ofMillis(1000))
                .filter((ex) -> ex instanceof MovieException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));
    }

    public Flux<Movie> getAllMovies_retry_when() {

        //var retryWhen = Retry.backoff(3, Duration.ofMillis(500));

        var movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        var movies = movieInfoFlux
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(movieList -> new Movie(movieInfo, movieList));
                }))

                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(retrySpec());


        return movies;
    }

    public Flux<Movie> getAllMovies_repeat() {


        var movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        var movies = movieInfoFlux
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(movieList -> new Movie(movieInfo, movieList));
                }))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(retrySpec())
                .repeat();
        //.repeat(1);

        return movies;
    }

    public Flux<Movie> getAllMovies_repeatN(long n) {


        var movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        var movies = movieInfoFlux
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(movieList -> new Movie(movieInfo, movieList));
                }))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(retrySpec())
                .repeat(n);

        return movies;
    }


    public Flux<Movie> getAllMovies_repeatWhen() {

        //var retryWhen = Retry.backoff(3, Duration.ofMillis(500));

        var movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        var movies = movieInfoFlux
                .flatMap((movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(movieList -> new Movie(movieInfo, movieList));
                }))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(retrySpec())
                .repeatWhen((flux) -> Flux.range(0, 1)
                        .delayElements(Duration.ofMillis(1000)));


        return movies;
    }

    public Flux<Movie> getAllMovieInfo_1() {

        return Flux.create(sink -> {
            CompletableFuture.supplyAsync(() -> movieInfoService.movieList())
                    .thenAccept((movieInfos -> movieInfos.forEach(movieInfo -> {
                        List<Review> reviewsList = reviewService.retrieveReviews(movieInfo.getMovieInfoId());
                        sink.next(new Movie(movieInfo, reviewsList));
                    })))
                    .thenRun(sink::complete);
        });
    }

    public Mono<Movie> getMovieById(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewList = reviewService.retrieveReviewsFlux(movieId)
                .collectList();

        return movieInfoMono.zipWith(reviewList, (movienfo, reviews) -> new Movie(movienfo, reviews))
                .onErrorMap((ex) -> {
                    System.out.println("Exception is " + ex);
                    ;
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                });
    }

    public Mono<Movie> getMovieById_usingFlatMap(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        return movieInfoMono
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(movieList -> new Movie(movieInfo, movieList));

                });
    }

    public Mono<Movie> getMovieById_RestClient(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoById_RestClient(movieId);
        var reviewList = reviewService.retrieveReviewsFlux_RestClient(movieId)
                .collectList();

        return movieInfoMono.zipWith(reviewList, (movienfo, reviews) -> new Movie( movienfo, reviews));
    }

    public Mono<Movie> getMovieById_withRevenue(long movieId) {

        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewList = reviewService.retrieveReviewsFlux(movieId)
                .collectList();
        var revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());

        return movieInfoMono.zipWith(reviewList, (movieInfo, reviews) -> new Movie(movieInfo, reviews))
                .zipWith(revenueMono, ((movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                }));
    }

    public Mono<Movie> getMovieById_1(long movieId) {

        Mono<Movie> movieMono = Mono.create((sink) -> {
            var movieInfoFuture = CompletableFuture.supplyAsync(() -> movieInfoService.retrieveMovieUsingId(movieId));

            var reviewsFuture = CompletableFuture.supplyAsync(() -> {
                return reviewService.retrieveReviews(movieId);
            });

            movieInfoFuture.thenCombine(reviewsFuture, (movieInfo, reviews) -> {
                return new Movie(movieInfo, reviews);
            })
                    .thenAccept(movie -> {
                        sink.success(movie);
                    });
        });
        return movieMono;
    }


}

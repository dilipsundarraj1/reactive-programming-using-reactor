package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.MovieInfo;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class MovieInfoService {

    private WebClient webClient;

    public MovieInfoService(WebClient webClient) {
        this.webClient = webClient;
    }

    public MovieInfoService(){

    }

    public Flux<MovieInfo> retrieveAllMovieInfo_RestClient(){

        return webClient.get().uri("/v1/movie_infos")
                .retrieve()
                .bodyToFlux(MovieInfo.class)
                .log();

    }

    public Mono<MovieInfo> retrieveMovieInfoById_RestClient(Long movieInfoId){

        return webClient.get().uri("/v1/movie_infos/{id}", movieInfoId)
                .retrieve()
                .bodyToMono(MovieInfo.class)
                .log();

    }

    public  Flux<MovieInfo> retrieveMoviesFlux(){

        var movieInfoList = List.of(new MovieInfo(100l, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(101L,"The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo(102L,"Dark Knight Rises", 2008, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        return Flux.fromIterable(movieInfoList);
    }


    public  Mono<MovieInfo> retrieveMovieInfoMonoUsingId(long movieId){

        var movie = new MovieInfo(movieId, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        return Mono.just(movie);
    }

    public  List<MovieInfo> movieList(){
        delay(1000);

        return List.of(new MovieInfo(100L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(101L,"The Dark Knight", 2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo(102L,"Dark Knight Rises", 2008, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));
    }

    public  MovieInfo retrieveMovieUsingId(long movieId){
        delay(1000);
        return new MovieInfo(movieId, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
    }

}

package com.learnreactiveprogramming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private MovieInfo movie;
    private List<Review> reviewList;
    private Revenue revenue;

    public Movie(MovieInfo movie, List<Review> reviewList) {
        this.movie = movie;
        this.reviewList = reviewList;
    }

}
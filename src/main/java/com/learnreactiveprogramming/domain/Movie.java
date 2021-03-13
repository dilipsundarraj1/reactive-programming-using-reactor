package com.learnreactiveprogramming.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private MovieInfo movieInfo;
    private List<Review> reviewList;
    private Revenue revenue;

    public Movie(MovieInfo movieInfo, List<Review> reviewList) {
        this.movieInfo = movieInfo;
        this.reviewList = reviewList;
    }

}

package com.learnreactiveprogramming.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Revenue {
    private Long movie_info_id;
    private double budget;
    private double boxOffice;
}

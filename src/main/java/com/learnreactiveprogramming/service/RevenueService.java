package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Revenue;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class RevenueService {

    public Revenue getRevenue(Long movieId){
        delay(1000); // simulating a network call ( DB or Rest call)
        return Revenue.builder()
                .movieId(movieId)
                .budget(1000000)
                .boxOffice(5000000)
                .build();

    }
}

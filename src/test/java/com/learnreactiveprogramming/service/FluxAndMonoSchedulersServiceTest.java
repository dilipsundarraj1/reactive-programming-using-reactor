package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class FluxAndMonoSchedulersServiceTest {

    FluxAndMonoSchedulersService fluxAndMonoThread = new FluxAndMonoSchedulersService();

    @Test
    void explore_publishOn() {

        //given

        //when
        var flux = fluxAndMonoThread.explore_publishOn();

        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();

    }

    @Test
    void explore_subscribeOn() {

        //given

        //when
        var flux = fluxAndMonoThread.
                explore_subscribeOn();

        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();

    }

    @Test
    void explore_subscribeOn_publishOn() {

        //given

        //when
        var flux = fluxAndMonoThread.
                explore_subscribeOn_publishOn();

        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();

    }
}
package com.learnreactiveprogramming;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirutalTimerTest {

    @Test
    void sampleFlux() {

        var flux = Flux.range(1,5)
                .delayElements(Duration.ofSeconds(1))
                .log();

        StepVerifier.withVirtualTime(()->flux)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }

    @Test
    void sampleFlux_WithVirtualTime() {


        VirtualTimeScheduler.getOrSet();

        var flux = Flux.range(1,5)
                .delayElements(Duration.ofSeconds(1))
                .log();

        StepVerifier.withVirtualTime(()->flux)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}

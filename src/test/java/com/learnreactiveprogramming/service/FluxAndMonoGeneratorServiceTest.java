package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.util.List;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService =
            new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        // given

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        // then
        StepVerifier.create(namesFlux)
                //.expectNext("alex", "ben", "chloe")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void nameMono() {
        // given

        // when
        var nameMono = fluxAndMonoGeneratorService.nameMono();

        // then
        StepVerifier.create(nameMono)
                .expectNext("alex")
                .verifyComplete();
    }

    @Test
    void namesFlux_map() {
        // given
        int stringLength = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {
        // given

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();

        // then
        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter() {
        // given
        int stringLength = 4;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesMono_map_filter(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {
        // given
        int stringLength = 3;

        // when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap_async() {
        // given
        int stringLength = 3;

        // when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);

        // then
        StepVerifier.create(namesFlux)
                //.expectNext("A","L","E","X","C","H","L","O","E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap() {
        // given
        int stringLength = 3;

        // when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap_virtualTimer() {
        // given
        VirtualTimeScheduler.getOrSet();
        int stringLength = 3;

        // when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength);

        // then
        StepVerifier.withVirtualTime(() -> namesFlux)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesMono_flatMap() {
        // given
        int stringLength = 3;

        // when
        var value = fluxAndMonoGeneratorService.namesMono_flatMap(stringLength);

        // then
        StepVerifier.create(value)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMono_flatMapMany() {
        // given
        int stringLength = 3;

        // when
        var value = fluxAndMonoGeneratorService.namesMono_flatMapMany(stringLength);

        // then
        StepVerifier.create(value)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        // given
        int stringLength = 3;

        // when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_1() {
        // given
        int stringLength = 6;

        // when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        // then
        StepVerifier.create(namesFlux)
                //.expectNext("A","L","E","X","C","H","L","O","E")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchifEmpty() {
        // given
        int stringLength = 6;

        // when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchifEmpty(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void namesMono_map_filter_switchIfEmpty() {
        // given
        int stringLength = 4;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesMono_map_filter_switchIfEmpty(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        // given

        // when
        var concatFlux = fluxAndMonoGeneratorService.explore_concat();

        // then
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatwith() {

        var concatWith = fluxAndMonoGeneratorService.explore_concatwith();

        StepVerifier.create(concatWith)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatwith_mono() {

        var concatWithMono = fluxAndMonoGeneratorService.explore_concatwith_mono();

        StepVerifier.create(concatWithMono)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_merge() {

        var exploreMerge = fluxAndMonoGeneratorService.explore_merge();

        StepVerifier.create(exploreMerge)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();

    }

    @Test
    void explore_mergeWith() {

        var exploreMergeWith = fluxAndMonoGeneratorService.explore_mergeWith();

        StepVerifier.create(exploreMergeWith)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void explore_mergeWith_mono() {

        var mergeWithMono = fluxAndMonoGeneratorService.explore_mergeWith_mono();

        StepVerifier.create(mergeWithMono)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_mergeSequential() {

        var mergeSequential = fluxAndMonoGeneratorService.explore_mergeSequential();

        StepVerifier.create(mergeSequential)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_zip() {

        var exploreZip = fluxAndMonoGeneratorService.explore_zip();

        StepVerifier.create(exploreZip)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zip_1() {

        var exploreZip = fluxAndMonoGeneratorService.explore_zip_1();

        StepVerifier.create(exploreZip)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void explore_zipWith() {

        var zipWith = fluxAndMonoGeneratorService.explore_zipWith();

        StepVerifier.create(zipWith)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zipWith_mono() {

        var zipWithMono = fluxAndMonoGeneratorService.explore_zipWith_mono();

        StepVerifier.create(zipWithMono)
                .expectNext("AB")
                .verifyComplete();
    }

    @Test
    void exception_flux() {
        var value = fluxAndMonoGeneratorService.exception_flux();

        StepVerifier.create(value)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void exception_flux_1() {
        var value = fluxAndMonoGeneratorService.exception_flux();

        StepVerifier.create(value)
                .expectNext("A", "B", "C")
                .expectError()
                .verify();
    }

    @Test
    void exception_flux_2() {
        var value = fluxAndMonoGeneratorService.exception_flux();

        StepVerifier.create(value)
                .expectNext("A", "B", "C")
                .expectErrorMessage("Exception Occured")
                .verify();
    }

    @Test
    void explore_OnErrorReturn() {
        var value = fluxAndMonoGeneratorService.explore_OnErrorReturn();

        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D")
                .verifyComplete();
    }

    @Test
    void explore_OnErrorResume() {
        var e = new IllegalStateException("Not a valid State");

        var value = fluxAndMonoGeneratorService.explore_OnErrorResume(e);

        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_OnErrorResume_1() {
        var e = new RuntimeException("Not a valid State");

        var value = fluxAndMonoGeneratorService.explore_OnErrorResume(e);

        StepVerifier.create(value)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void explore_OnErrorContinue() {
        var value = fluxAndMonoGeneratorService.explore_OnErrorContinue();

        StepVerifier.create(value)
                .expectNext("A", "C", "D")
                .verifyComplete();
    }

    @Test
    void explore_OnErrorMap() {
        var value = fluxAndMonoGeneratorService.explore_OnErrorMap();

        StepVerifier.create(value)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void explore_doOnError() {
        var value = fluxAndMonoGeneratorService.explore_doOnError();

        StepVerifier.create(value)
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void explore_Mono_OnErrorReturn() {
        var value = fluxAndMonoGeneratorService.explore_Mono_OnErrorReturn();

        StepVerifier.create(value)
                .expectNext("abc")
                .verifyComplete();
    }

    @Test
    void exception_mono_onErrorContinue_abc() {
        String input = "abc";

        var value = fluxAndMonoGeneratorService.exception_mono_onErrorContinue(input);

        StepVerifier.create(value)
                .verifyComplete();
    }

    @Test
    void exception_mono_onErrorContinue_reactor() {
        String input = "reactor";

        var value = fluxAndMonoGeneratorService.exception_mono_onErrorContinue(input);

        StepVerifier.create(value)
                .expectNext("reactor")
                .verifyComplete();
    }

    @Test
    void explore_generate() {

        var flux = fluxAndMonoGeneratorService.explore_generate().log();

        StepVerifier.create(flux)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void exlpore_create() {

        var flux = fluxAndMonoGeneratorService.exlpore_create().log();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void exlpore_create_mono() {

        var mono = fluxAndMonoGeneratorService.exlpore_create_mono().log();

        StepVerifier.create(mono)
                .expectNext("alex")
                .verifyComplete();

    }

    @Test
    void explore_handle() {

        var flux = fluxAndMonoGeneratorService.explore_handle().log();

        StepVerifier.create(flux)
                .expectNextCount(2)
                .verifyComplete();
    }
}

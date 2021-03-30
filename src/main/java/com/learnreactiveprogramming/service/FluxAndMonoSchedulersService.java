package com.learnreactiveprogramming.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    public Flux<String> explore_publishOn() {
        // start without publish on
        // add publishon Schedulers.parallel()
        // add publishon Schedulers.boundedElastic() for the second flux


        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.boundedElastic())
                .map(this::upperCase)
                .map((s) -> {
                    log.info("Value of s is {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public ParallelFlux<String> explore_parallel() {

        log.info("no of cores : {}", Runtime.getRuntime().availableProcessors());

        var namesFlux = Flux.fromIterable(namesList)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();

        return namesFlux;
    }

    public Flux<String> explore_parallel_usingFlatMap() {

        var namesFlux = Flux.fromIterable(namesList)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                        .log();

        return namesFlux;
    }

    public Flux<String> explore_parallel_usingFlatMap_1() {
        // start without publish on
        // add publishon Schedulers.parallel()
        // add publishon Schedulers.boundedElastic() for the second flux

        var namesFlux = Flux.fromIterable(namesList)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .map((s) -> {
                    log.info("Value of s is {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> explore_parallel_usingFlatMapSequential() {

        var namesFlux = Flux.fromIterable(namesList)
                .flatMapSequential(name -> {
                    return Mono.just(name)
                            .map(this::upperCase)
                            .subscribeOn(Schedulers.parallel());

                })
                .log();

        return namesFlux;
    }



    public ParallelFlux<String> explore_parallel_1() {
        // start without publish on
        // add publishon Schedulers.parallel()
        // add publishon Schedulers.boundedElastic() for the second flux


        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.boundedElastic())
                .map(this::upperCase)
                .map((s) -> {
                    log.info("Value of s is {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1)
                .parallel()
                .runOn(Schedulers.parallel());
    }


    public Flux<String> explore_subscribeOn() {
        var namesFlux = flux1()
                .map((s) -> {
                    log.info("Value of s is {}", s);
                    return s;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .log();

        var namesFlux1 = flux2()
                .map((s) -> {
                    log.info("Value of s is {}", s);
                    return s;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map((s) -> {
                    log.info("Value of s after boundedElastic is {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> explore_subscribeOn_publishOn() {
        var namesFlux = flux1()
                .map((s) -> {
                    log.info("Value of s is {}", s);
                    return s;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.parallel())
                .map((s) -> {
                    log.info("Value of s after publishOn is {}", s);
                    return s;
                })
                .log();

        var namesFlux1 = flux2()
                .map((s) -> {
                    log.info("Value of s is {}", s);
                    return s;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .publishOn(Schedulers.parallel())
                .map((s) -> {
                    log.info("Value of s after publishOn is {}", s);
                    return s;
                })
                .log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> flux1() {
        var namesFlux = Flux.fromIterable(namesList)
                .map(this::upperCase);
        return namesFlux;
    }

    public Flux<String> flux2() {
        var namesFlux = Flux.fromIterable(namesList1)
                .map(this::upperCase);
        return namesFlux;
    }

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

    public static void main(String[] args) throws InterruptedException {

        Flux.just("hello")
                .doOnNext(v -> System.out.println("just " + Thread.currentThread().getName()))
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(v -> System.out.println("publish 0" + Thread.currentThread().getName()))
                .delayElements(Duration.ofMillis(500))
                .doOnNext(v -> System.out.println("publish 1" + Thread.currentThread().getName()))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(v -> System.out.println(v + " delayed " + Thread.currentThread().getName()));

        Thread.sleep(5000);
    }
}

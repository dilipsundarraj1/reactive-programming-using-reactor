package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoGeneratorService {

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> System.out.println("Name is : " + name));

        fluxAndMonoGeneratorService.nameMono()
                .subscribe(name -> System.out.println("Mono name is :" + name));
    }

    public static List<String> names() {
        delay(1000);
        return List.of("alex", "ben", "chloe");
    }

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .log();
    }

    public Mono<String> nameMono() {
        return Mono.just("alex")
                .log();
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .defaultIfEmpty("default")
                .log();
    }

    public Mono<String> namesMono_map_filter_switchIfEmpty(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .switchIfEmpty(Mono.just("default"))
                .log();
    }

    public Mono<List<String>> namesMono_flatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }

    public Flux<String> namesFlux_map(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                //.map(s -> s.toUpperCase())
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                .doOnNext(name -> {
                    System.out.println("Name is : " + name);
                    name.toLowerCase();
                })
                .doOnSubscribe(s -> {
                    System.out.println("Subscription is : " + s);
                })
                .doOnComplete(() -> {
                    System.out.println("Inside the complete callback");
                })
                .doFinally(signalType -> {
                    System.out.println("Inside doFinally : " + signalType);
                })
                .log();
    }

    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Flux<String> namesFlux_flatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                //.map(s -> s.toUpperCase())
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitString(s))
                .log();
    }

    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                //.map(s -> s.toUpperCase())
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitString_withDelay(s))
                .log();
    }

    public Flux<String> namesFlux_concatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                //.map(s -> s.toUpperCase())
                .filter(s -> s.length() > stringLength)
                .concatMap(s -> splitString_withDelay(s))
                .log();
    }

    public Flux<String> namesFlux_transform(int stringLength) {

        Function<Flux<String>, Flux<String>> filtermap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filtermap)
                .flatMap(s -> splitString(s))
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFlux_transform_switchifEmpty(int stringLength) {

        Function<Flux<String>, Flux<String>> filtermap = name ->
                name.map(String::toUpperCase)
                        .filter(s -> s.length() > stringLength)
                        .flatMap(s -> splitString(s));

        var defaultFlux = Flux.just("default")
                .transform(filtermap);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filtermap)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> explore_concat() {

        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> explore_concatwith() {

        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> explore_concatwith_mono() {

        var aMono = Mono.just("A");
        var bMono = Flux.just("B");

        return aMono.concatWith(bMono);
    }

    public Flux<String> explore_merge() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> explore_mergeWith() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_mergeWith_mono() {

        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.mergeWith(bMono).log();
    }

    public Flux<String> explore_mergeSequential() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux).log();
        //return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_zip() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log();
        //return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_zip_1() {

        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        var _123Flux = Flux.just("1", "2", "3");
        var _456Flux = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();
        //return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_zipWith() {

        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux, (first, second) -> first + second)
                .log();
    }

    public Mono<String> explore_zipWith_mono() {

        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2())
                .log();
    }

    public Flux<String> exception_flux() {

        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .log();
    }

    public Flux<String> explore_OnErrorReturn() {

        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception Occured")))
                .onErrorReturn("D")
                .log();
    }

    public Flux<String> explore_OnErrorResume(Exception e) {

        var recoveryFlux = Flux.just("D", "E", "F");

        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .onErrorResume(ex -> {
                    log.error("Exception is ", ex);
                    if (ex instanceof IllegalStateException)
                        return recoveryFlux;
                    else
                        return Flux.error(ex);
                })
                .log();
    }

    public Flux<String> explore_OnErrorContinue() {

        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B"))
                        throw new IllegalStateException("Exception Occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue((ex, name) -> {
                    log.error("Exception is ", ex);
                    log.info("name is {}", name);
                })
                .log();
    }

    public Flux<String> explore_OnErrorMap() {

        return Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B"))
                        throw new IllegalStateException("Exception Occurred");
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorMap((ex) -> {
                    log.error("Exception is ", ex);
                    return new ReactorException(ex, ex.getMessage());
                })
                .log();
    }

    public Flux<String> explore_doOnError() {

        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception Occured")))
                .doOnError(ex -> {
                    log.error("Exception is ", ex);
                })
                .log();
    }

    public Mono<Object> explore_Mono_OnErrorReturn() {

        return Mono.just("A")
                .map(value -> {
                    throw new RuntimeException("Exception Occured");
                })
                .onErrorReturn("abc")
                .log();
    }

    public Mono<String> exception_mono_onErrorContinue(String input) {

        return Mono.just(input)
                .map(value -> {
                    if ("abc".equals(value))
                        throw new RuntimeException("abc catched");
                    else
                        return value;
                })
                .onErrorContinue((ex, name) -> {
                    log.error("Exception is ", ex);
                    log.info("name is {}", name);
                })
                .log();
    }

    public Flux<Integer> explore_generate() {

        return Flux.generate(
                () -> 1, (state, sink) -> {
                    sink.next(state * 2);

                    if (state == 10) {
                        sink.complete();
                    }
                    return state + 1;
                }
        );
    }

    public Flux<String> exlpore_create() {
        return Flux.create(fluxSink -> {
            //names().forEach(fluxSink::next);
            CompletableFuture.supplyAsync(() -> names())
                    .thenAccept(names -> {
                        names.forEach(fluxSink::next);
                    })
                    .thenRun(() -> sendEvents(fluxSink));
        });
    }

    public Mono<String> exlpore_create_mono() {
        return Mono.create(monoSink -> {
            monoSink.success("alex");
        });
    }

    public void sendEvents(FluxSink<String> fluxSink) {
        CompletableFuture.supplyAsync(() -> names())
                .thenAccept(names -> {
                    names.forEach(fluxSink::next);
                })
                .thenRun(fluxSink::complete);
    }

    public Flux<String> explore_handle() {

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .handle((name, sink) -> {
                    if(name.length()>3){
                        sink.next(name.toUpperCase());
                    }
                });
    }

    private Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    private Flux<String> splitString_withDelay(String name) {
        var charArray = name.split("");
        //int delay = new Random().nextInt(1000);
        var delay = 1000;
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }
}

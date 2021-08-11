package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoGeneratorService {


    public Flux<String> namesFlux() {
        var namesList = List.of("alex", "ben", "chloe");
        //return Flux.just("alex", "ben", "chloe");
        return Flux.fromIterable(namesList); // coming from a db or remote service

    }

    public Flux<String> namesFlux_immutablity() {
        var namesList = List.of("alex", "ben", "chloe");
        //return Flux.just("alex", "ben", "chloe");
        var namesFlux = Flux.fromIterable(namesList);
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }


    public Flux<String> namesFlux_map(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe");
        //return Flux.just("alex", "ben", "chloe");

        //Flux.empty()
        return Flux.fromIterable(namesList)
                //.map(s -> s.toUpperCase())
                .map(String::toUpperCase)
                .delayElements(Duration.ofMillis(500))
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                .doOnNext(name -> {
                    System.out.println("name is : " + name);
                    name = name.toLowerCase();
                })
                .doOnSubscribe(s -> {
                    System.out.println("Subscription  is : " + s);
                })
                .doOnComplete(() -> {
                    System.out.println("Completed sending all the items.");
                })
                .doFinally((signalType) -> {
                    System.out.println("value is : " + signalType);
                })
                .defaultIfEmpty("default");
    }

    public Mono<String> namesMono() {

        return Mono.just("alex");

    }

    public Mono<String> namesMono_map_filter(int stirngLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stirngLength)
                .defaultIfEmpty("default");

    }


    /**
     * @param stringLength
     */
    public Flux<String> namesFlux_flatmap(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe"); // a, l, e , x
        return Flux.fromIterable(namesList)
                //.map(s -> s.toUpperCase())
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                // ALEX,CHLOE -> A, L, E, X, C, H, L , O, E
                .flatMap(this::splitString);


    }

    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe"); // a, l, e , x
        return Flux.fromIterable(namesList)
                //.map(s -> s.toUpperCase())
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString_withDelay);


    }

    public Flux<String> namesFlux_concatmap(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe"); // a, l, e , x
        return Flux.fromIterable(namesList)
                //.map(s -> s.toUpperCase())
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                //.flatMap((name)-> splitString(name));
                .concatMap(this::splitString_withDelay);

    }

    public Mono<List<String>> namesMono_flatmap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono); //Mono<List of A, L, E  X>
    }

    public Flux<String> namesMono_flatmapMany(int stringLength) {
        return Mono.just("alex")
                //.map(s -> s.toUpperCase())
                .map(String::toUpperCase)
                .flatMapMany(this::splitString_withDelay);
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        return Mono.just(List.of(charArray))
                .delayElement(Duration.ofSeconds(1));
    }


    public Flux<String> namesFlux_transform(int stringLength) {

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        var namesList = List.of("alex", "ben", "chloe"); // a, l, e , x
        return Flux.fromIterable(namesList)
                .transform(filterMap) // gives u the opportunity to combine multiple operations using a single call.
                .flatMap(this::splitString)
                .defaultIfEmpty("default");
        //using "map" would give the return type as Flux<Flux<String>

    }


    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString);

        var defaultFlux = Flux.just("default")
                .transform(filterMap); //"D","E","F","A","U","L","T"

        var namesList = List.of("alex", "ben", "chloe"); // a, l, e , x
        return Flux.fromIterable(namesList)
                .transform(filterMap) // gives u the opportunity to combine multiple operations using a single call.
                .switchIfEmpty(defaultFlux);
        //using "map" would give the return type as Flux<Flux<String>

    }


    public Flux<String> namesFlux_transform_concatwith(int stringLength) {
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s);

        var namesList = List.of("alex", "ben", "chloe"); // a, l, e , x
        var flux1 = Flux.fromIterable(namesList)
                .transform(filterMap);

        var flux2 = flux1.concatWith(Flux.just("anna")
                .transform(filterMap));

        return flux2;

    }

    public Mono<String> name_defaultIfEmpty() {

        return Mono.<String>empty() // db or rest call
                .defaultIfEmpty("Default");

    }


    public Mono<String> name_switchIfEmpty() {

        Mono<String> defaultMono = Mono.just("Default");
        return Mono.<String>empty() // db or rest call
                .switchIfEmpty(defaultMono);

    }

    // "A", "B", "C", "D", "E", "F"
    public Flux<String> explore_concat() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux);

    }


    // "A", "B", "C", "D", "E", "F"
    public Flux<String> explore_concatWith() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux).log();


    }

    public Flux<String> explore_concatWith_mono() {

        var aMono = Mono.just("A");

        var bMono = Flux.just("B");

        return aMono.concatWith(bMono);

    }

    // "A", "D", "B", "E", "C", "F"
    // Flux is subscribed early
    public Flux<String> explore_merge() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.merge(abcFlux, defFlux).log();


    }

    // "A", "D", "B", "E", "C", "F"
    // Flux is subscribed early
    public Flux<String> explore_mergeWith() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return abcFlux.mergeWith(defFlux).log();


    }

    public Flux<String> explore_mergeWith_mono() {

        var aMono = Mono.just("A");

        var bMono = Flux.just("B");

        return aMono.mergeWith(bMono);


    }

    // "A","B","C","D","E","F"
    // Flux is subscribed early
    public Flux<String> explore_mergeSequential() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));

        return Flux.mergeSequential(abcFlux, defFlux).log();

    }

    // AD, BE, FC
    public Flux<String> explore_zip() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second);


    }

    // AD14, BE25, CF36
    public Flux<String> explore_zip_1() {

        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        var flux3 = Flux.just("1", "2", "3");
        var flux4 = Flux.just("4", "5", "6");

        return Flux.zip(abcFlux, defFlux, flux3, flux4)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4());


    }

    public Flux<String> explore_zip_2() {

        var aMono = Mono.just("A");
        var bMono = Mono.just("B");


        return Flux.zip(aMono, bMono, (first, second) -> first + second);


    }

    // AD, BE, CF
    public Flux<String> explore_zipWith() {

        var abcFlux = Flux.just("A", "B", "C");

        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.zipWith(defFlux, (first, second) -> first + second);


    }

    public Mono<String> explore_zipWith_mono() {

        var aMono = Mono.just("A");

        var bMono = Mono.just("B");

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2());


    }

    public Flux<String> exception_flux() {

        var flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")));
        return flux;

    }


    /**
     * This provides a single fallback value
     *
     * @return
     */
    public Flux<String> explore_OnErrorReturn() {

        var flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new IllegalStateException("Exception Occurred")))
                .onErrorReturn("D"); // defualt value

        return flux;

    }

    /**
     * This provides a fallback value as a Reactive Stream
     *
     * @param e
     * @return
     */
    public Flux<String> explore_OnErrorResume(Exception e) {

        var recoveryFlux = Flux.just("D", "E", "F");

        var flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .onErrorResume((exception) -> {
                    log.error("Exception is ", exception);
                    if (exception instanceof IllegalStateException)
                        return recoveryFlux;
                    else
                        return Flux.error(exception);
                });

        return flux;

    }

    /**
     * This helps to drop elements thats causing the issue and move on with the other elements
     *
     * @return
     */
    public Flux<String> explore_OnErrorContinue() {

        var flux = Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B")) {
                        throw new IllegalStateException("Exception Occurred");
                    }
                    return name;
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue((exception, value) -> {
                    System.out.println("Value is : " + value);
                    System.out.println("Exception is : " + exception.getMessage());
                });


        return flux;

    }


    /**
     * Used to tranform the error from one type to another
     *
     * @param e
     * @return
     */
    public Flux<String> explore_OnErrorMap(Exception e) {

        var flux = Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B")) {
                        throw new IllegalStateException("Exception Occurred");
                    }
                    return name;
                })
                .onErrorMap((exception) -> {
                    // log.error("Exception is : " , exception);
                    // difference between errorResume and this one is that you dont need to add
                    // Flux.error() to throw the exception
                    return new ReactorException(exception, exception.getMessage());
                });

        return flux;

    }


    /**
     * Used to tranform the error from one type to another
     *
     * @param e
     * @return
     */
    public Flux<String> explore_OnErrorMap_checkpoint(Exception e) {

        var flux = /*Flux.just("A", "B", "C")
                .map(name -> {
                    if (name.equals("B")) {
                        throw new IllegalStateException("Exception Occurred");
                    }
                    return name;
                })*/
                Flux.just("A")
                        .concatWith(Flux.error(e))
                //.checkpoint("errorSpot")
                .onErrorMap((exception) -> {
                    log.error("Exception is : " , exception);
                    // difference between errorResume and this one is that you dont need to add
                    // Flux.error() to throw the exception
                    return new ReactorException(exception, exception.getMessage());
                });

        return flux;

    }

    public void exception() {
        try {
            // code statements
        } catch (Exception e) {
            //log the exception
            throw e;
        }
    }

    public Flux<String> explore_doOnError(Exception e) {

        var flux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .doOnError((exception) -> {
                    System.out.println("Exception is : " + e);
                    //Write any logic you would like to perform when an exception happens
                });

        return flux;

    }


    public Mono<Object> exception_mono_exception() {

        var mono = Mono.just("B")
                .map(value -> {
                    throw new RuntimeException("Exception Occurred");
                });
        return mono;

    }


    /**
     * This operator can be used to provide a default value when an error occurs
     *
     * @return
     */
    public Mono<Object> exception_mono_onErrorReturn() {

        return Mono.just("B")
                .map(value -> {
                    throw new RuntimeException("Exception Occurred");
                }).onErrorReturn("abc");
    }


    /***
     *  This operator can be used to resume from an exception.
     *  The recovery value here will be a Mono instead of the direct value
     * @return
     */
    public Mono<Object> exception_mono_onErrorResume(Exception e) {

        var mono = Mono.error(e);

        return mono.onErrorResume((ex) -> {
            System.out.println("Exception is " + ex);
            if (ex instanceof IllegalStateException)
                return Mono.just("abc");
            else
                return Mono.error(ex);
        });
    }

    /**
     * This operator can be used to map the exception to another user defined or custom exception
     *
     * @param e
     * @return
     */
    public Mono<Object> exception_mono_onErrorMap(Exception e) {

        return Mono.just("B")
                .map(value -> {
                    throw new RuntimeException("Exception Occurred");
                }).onErrorMap(ex -> {
                    System.out.println("Exception is " + ex);
                    return new ReactorException(ex, ex.getMessage());
                });
    }

    /**
     * This operator allows the reactive stream to continue emitting elements when an error occured in the flow
     *
     * @return
     */
    public Mono<String> exception_mono_onErrorContinue(String input) {

        return Mono.just(input).
                map(data -> {
                    if (data.equals("abc"))
                        throw new RuntimeException("Exception Occurred");
                    else
                        return data;
                }).
                onErrorContinue((ex, val) -> {
                    log.error("Exception is " + ex);
                    log.error("Value that caused the exception is " + val);

                });
    }


    public Flux<Integer> explore_generate() {

        Flux<Integer> flux = Flux.generate(
                () -> 1,
                (state, sink) -> {
                    sink.next(state * 2);
                    if (state == 10) {
                        sink.complete();
                    }
                    return state + 1;
                });

        return flux;
    }

    public static List<String> names() {
        delay(1000);
        return List.of("alex", "ben", "chloe");
    }

    public Flux<String> explore_create() {

        return Flux.create(sink -> {
            //1. Start with this code
            /*names().forEach(sink::next);
            sink.complete();*/

            //2. Finish with this code
            CompletableFuture.supplyAsync(() -> names()) // place the blocking call inside the create function
                    .thenAccept(names -> {
                        names.forEach(sink::next);
                    })
                    .thenRun(()-> sendEvents(sink))
                    .whenComplete((data, exception) -> {
                        sink.error(exception);
                    });

            //sendEvents(sink);

        }, FluxSink.OverflowStrategy.BUFFER);
    }

    public Mono<String> explore_create_mono() {
        Mono<String> mono = Mono.create(sink -> {
            CompletableFuture.supplyAsync(()->name())
                    .thenAccept(name->sink.success(name));
        });
        return mono;
    }

    private String name() {
        delay(1000);
        return  "alex";
    }

    public Flux<String> explore_push() {

        Flux<String> flux = Flux.push(sink -> {
            CompletableFuture.supplyAsync(() -> names()) // place the blocking call inside the create function
                    .thenAccept(names -> {
                        names.forEach((s) -> {
                            sink.next(s);
                        });
                    })
                    .thenRun(()-> sendEvents(sink))
                    .whenComplete((data, exception) -> {
                        sink.error(exception);
                    });

           // sendEvents(sink);
        });
        return flux;
    }

    public Flux<String> explore_handle() {
        var namesList = List.of("alex", "ben", "chloe");
        return Flux.fromIterable(namesList)
                .handle((name,sink)->{
                    if(name.length()>3)
                        sink.next(name);
                });

    }


    public Mono<String> explore_mono_create() {

        Mono<String> abc = Mono.create(sink -> {
            delay(1000);
            sink.success("abc");
        });

        return abc;
    }

    public void sendEvents(FluxSink<String> sink) {
        {
            CompletableFuture.supplyAsync(() -> names()) // place the blocking call inside the create function
                    .thenAccept(names -> {
                        names.forEach((s) -> {
                            sink.next(s);
                        });
                    })
                    .thenRun(() -> {
                        sink.complete();
                    });
        }
    }

    /***
     * ALEX -> FLux(A,L,E,X)
     * @param name
     * @return
     */
    private Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    private Flux<String> splitString_withDelay(String name) {
        var delay = new Random().nextInt(1000);
        var charArray = name.split("");
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(delay));
    }

    private Flux<String> delayString(String string) {

        var delay = new Random().nextInt(1000);
        return Flux.just(string)
                .delayElements(Duration.ofMillis(delay));
    }

    /**
     * @param stringLength
     * @return AL, EX, CH, LO, E
     */
    public Flux<String> namesFlux_flatmap_sequential(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe"); // a, l, e , x
        return Flux.fromIterable(namesList)
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString)
                .log();
        //using "map" would give the return type as Flux<Flux<String>

    }

    private Flux<String> lowercase(Flux<String> stringFlux) {
        delay(1000);
        return stringFlux.map(String::toLowerCase);
    }


    public Flux<String> namesFlux_delay(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe");

        return Flux.fromIterable(namesList)
                .delayElements(Duration.ofSeconds(1))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s);
    }

    public Flux<Integer> range(int max) {

        return Flux.range(0, max);
    }


    public Flux<Integer> generateLongFlux(int maxNum) {

        return Flux.range(0, maxNum);
    }

    public Flux<Integer> namesFlux1() {

        return Flux.fromIterable(List.of("Alex", "ben", "chloe"))
                .map(String::length)
                /*.publishOn(Schedulers.boundedElastic())
                .map(length -> {
                    delay(1000);
                    return length;
                })*/
                .log();
    }

    public Flux<Integer> namesFlux_subscribeOn() {

        return Flux.fromIterable(List.of("Alex", "ben", "chloe"))
                .map(String::length)
                .subscribeOn(Schedulers.boundedElastic())
                .map(length -> {
                    delay(1000);
                    return length;
                })
                .log();
    }

    public Flux<Integer> namesFlux_subscribeOn_publishOn() {

        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        return Flux.fromIterable(List.of("Alex", "ben", "chloe"))
                .map(name -> {
                    log.info("inside first map");
                    return name.length();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(length -> {
                    log.info("inside second map");
                    return length.intValue();
                })
                .publishOn(s)
                .map(length -> {
                    log.info("inside third map");
                    delay(1000);
                    return length;
                })
                .log();
    }

    public Flux<Integer> generateLongFlux_withException(int maxNum) {

        return Flux.range(0, maxNum)
                .map(i -> {
                    if (i == 5) {
                        throw new IllegalStateException("Not allowed for number : " + i);
                    }
                    return i;
                });
    }

    public Flux<Integer> generateLongFlux_withException_checkpoint(int maxNum) {

        return Flux.range(0, maxNum)
                .map(i -> {
                    if (i == 5) {
                        throw new IllegalStateException("Not allowed for number : " + i);
                    }
                    return i;
                })
                .checkpoint("flux_error", true);
    }

    public Flux<Integer> generateLongFlux_withDelay(int maxNum) {

        return Flux.range(0, maxNum)
                .delayElements(Duration.ofSeconds(1));
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux().log();

        namesFlux.subscribe((name) -> {
            System.out.println("Name is : " + name);
        });

        Mono<String> namesMono = fluxAndMonoGeneratorService.namesMono().log();

        namesMono.subscribe((name) -> {
            System.out.println("Name is : " + name);
        });
    }


}

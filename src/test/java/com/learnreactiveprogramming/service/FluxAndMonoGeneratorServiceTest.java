package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ReactorException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;
import reactor.tools.agent.ReactorDebugAgent;

import java.time.Duration;
import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {

        //given

        //when
        var stringFlux = fluxAndMonoGeneratorService.namesFlux();

        //then
        StepVerifier.create(stringFlux)
                //.expectNext("alex", "ben", "chloe")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void namesFlux_Immutability() {

        //given

        //when
        var stringFlux = fluxAndMonoGeneratorService.namesFlux_immutablity()
                .log();

        //then
        StepVerifier.create(stringFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNextCount(3)
                .verifyComplete();


    }

    @Test
    void namesMono() {

        //given
        //when
        var stringMono = fluxAndMonoGeneratorService.namesMono();

        //then
        StepVerifier.create(stringMono)
                .expectNext("alex")
                .verifyComplete();

    }

    @Test
    void namesMono_map_filter() {

        //given
        int stringLength = 3;

        //when
        var stringMono = fluxAndMonoGeneratorService.namesMono_map_filter(stringLength);

        //then
        StepVerifier.create(stringMono)
                .expectNext("ALEX")
                .verifyComplete();

    }

    @Test
    void namesMono_map_empty() {

        //given
        int stringLength = 4;

        //when
        var stringMono = fluxAndMonoGeneratorService.namesMono_map_filter(stringLength);

        //then
        StepVerifier.create(stringMono)
                .expectNext("default")
                .verifyComplete();

    }


    @Test
    void namesFlux_map() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();

    }

    @Test
    void namesFlux_flatmap() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();

    }

    @Test
    void namesFlux_flatmap_async() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                /*.expectNext("0-A", "1-L", "2-E", "3-X")
                .expectNextCount(5)*/
                .expectNextCount(9)
                .verifyComplete();

    }

    @Test
    void namesFlux_concatMap() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                //expectNext("0-A", "1-L", "2-E", "3-X")
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_concatmap_withVirtualTime() {
        //given
        VirtualTimeScheduler.getOrSet();
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatmap(stringLength);

        //then
        StepVerifier.withVirtualTime(()-> namesFlux)
                .thenAwait(Duration.ofSeconds(10))
                .expectNext("A","L","E","X","C","H","L","O","E")
                //.expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMono_flatmap() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesMono_flatmap(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();

    }

    @Test
    void namesMono_flatmapMany() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesMono_flatmapMany(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();

    }


    @Test
    void namesFlux_transform() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_transform_1() {

        //given
        int stringLength = 6;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("default")
                //.expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_transform_switchIfEmpty() {

        //given
        int stringLength = 6;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                //.expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_transform_concatwith() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_concatwith(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNext("4-ALEX", "5-CHLOE", "4-ANNA")
                .verifyComplete();

    }

    @Test
    void name_defaultIfEmpty() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.name_defaultIfEmpty();

        //then
        StepVerifier.create(value)
                .expectNext("Default")
                .verifyComplete();

    }

    @Test
    void name_switchIfEmpty() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.name_switchIfEmpty();

        //then
        StepVerifier.create(value)
                .expectNext("Default")
                .verifyComplete();

    }

    @Test
    void explore_concat() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_concat();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }


    @Test
    void explore_concatWith() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_concatWith();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }

    @Test
    void explore_concat_mono() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_concatWith_mono();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B")
                .verifyComplete();

    }

    @Test
    void explore_merge() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_merge();

        //then
        StepVerifier.create(value)
                // .expectNext("A", "B", "C", "D", "E", "F")
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();

    }

    @Test
    void explore_mergeWith() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_mergeWith();

        //then
        StepVerifier.create(value)

                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();

    }

    @Test
    void explore_mergeWith_mono() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_mergeWith_mono();

        //then
        StepVerifier.create(value)

                .expectNext("A", "B")
                .verifyComplete();

    }

    @Test
    void explore_mergeSequential() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_mergeSequential();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }

    @Test
    void explore_zip() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zip().log();

        //then
        StepVerifier.create(value)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();

    }

    @Test
    void explore_zip_1() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zip_1().log();

        //then
        StepVerifier.create(value)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();

    }


    @Test
    void explore_zip_2() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zip_2().log();

        //then
        StepVerifier.create(value)
                .expectNext("AB")
                .verifyComplete();

    }

    @Test
    void explore_zipWith() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zipWith().log();

        //then
        StepVerifier.create(value)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();

    }

    @Test
    void explore_zipWith_mono() {

        //given

        //when
        var value = fluxAndMonoGeneratorService.explore_zipWith_mono().log();

        //then
        StepVerifier.create(value)
                .expectNext("AB")
                .verifyComplete();

    }

    @Test
    void exception_flux() {

        //given

        //when
        var flux = fluxAndMonoGeneratorService.exception_flux();

        //then
        StepVerifier.create(flux)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify(); // you cannot do a verify complete in here

    }

    @Test
    void exception_flux_1() {

        //given

        //when
        var flux = fluxAndMonoGeneratorService.exception_flux();

        //then
        StepVerifier.create(flux)
                .expectNext("A", "B", "C")
                .expectError()
                .verify(); // you cannot do a verify complete in here

    }

    @Test
    void exception_flux_2() {

        //given

        //when
        var flux = fluxAndMonoGeneratorService.exception_flux();

        //then
        StepVerifier.create(flux)
                .expectNext("A", "B", "C")
                .expectErrorMessage("Exception Occurred")
                .verify(); // you cannot do a verify complete in here

    }


    @Test
    void explore_OnErrorReturn() {

        //given

        //when
        var flux = fluxAndMonoGeneratorService.explore_OnErrorReturn().log();

        //then
        StepVerifier.create(flux)
                .expectNext("A", "B", "C", "D")
                .verifyComplete();

    }


    @Test
    void explore_OnErrorResume() {

        //given
        var e = new IllegalStateException("Not a valid state");

        //when
        var flux = fluxAndMonoGeneratorService.explore_OnErrorResume(e).log();

        //then
        StepVerifier.create(flux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }

    @Test
    void explore_OnErrorResume_1() {

        //given
        var e = new RuntimeException("Not a valid state");

        //when
        var flux = fluxAndMonoGeneratorService.explore_OnErrorResume(e).log();

        //then
        StepVerifier.create(flux)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();

    }

    @Test
    void explore_OnErrorMap() {

        //given
        var e = new RuntimeException("Not a valid state");

        //when
        var flux = fluxAndMonoGeneratorService.explore_OnErrorMap(e)
                .log();

        //then
        StepVerifier.create(flux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void explore_OnErrorMap_checkpoint() {

        /*Error has been observed at the following site(s):
	        |_ checkpoint ⇢ errorspot*/
        //given
        // In production, it could the code or the data thats executing in the run time that might throw the exception
        var e = new RuntimeException("Not a valid state");

        //when
        var flux = fluxAndMonoGeneratorService.explore_OnErrorMap_checkpoint(e).log();

        //then
        StepVerifier.create(flux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }


    @Test
    /**
     * This gives the visibility of which operator caused the problem
     * This operator gives the "Assembly trace" which is not available when you use chekpoint
     * This also gives you the line that caused the problem
     */
    void explore_OnErrorMap_onOperatorDebug() {

        //You will see the below in the code
/*
        Error has been observed at the following site(s):
	|_      Flux.error ⇢ at com.learnreactiveprogramming.service.FluxAndMonoGeneratorService.explore_OnErrorMap_checkpoint(FluxAndMonoGeneratorService.java:336)
	|_ Flux.concatWith ⇢ at com.learnreactiveprogramming.service.FluxAndMonoGeneratorService.explore_OnErrorMap_checkpoint(FluxAndMonoGeneratorService.java:336)
	|_      checkpoint ⇢ errorspot
*/

        //given
        Hooks.onOperatorDebug();
        var e = new RuntimeException("Not a valid state");

        //when
        var flux = fluxAndMonoGeneratorService.explore_OnErrorMap_checkpoint(e).log();

        //then
        StepVerifier.create(flux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }


    @Test
    /**
     * This gives the visibility of which operator caused the problem without any performance overhead
     */
    void explore_OnErrorMap_reactorDebugAgent() {

        //given
        ReactorDebugAgent.init();
        ReactorDebugAgent.processExistingClasses();
        var e = new RuntimeException("Not a valid state");

        //when
        var flux = fluxAndMonoGeneratorService.explore_OnErrorMap_checkpoint(e).log();

        //then
        StepVerifier.create(flux)
                .expectNext("A")
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void doOnError() {

        //given
        var e = new RuntimeException("Not a valid state");

        //when
        var flux = fluxAndMonoGeneratorService.explore_doOnError(e);

        //then
        StepVerifier.create(flux)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();


    }

    @Test
    void explore_OnErrorContinue() {

        //given

        //when
        var flux = fluxAndMonoGeneratorService.explore_OnErrorContinue().log();

        //then
        StepVerifier.create(flux)
                .expectNext("A", "C", "D")
                .verifyComplete();

    }


    @Test
    void exception_mono() {

        //given

        //when
        var mono = fluxAndMonoGeneratorService.exception_mono_exception();

        //then
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();

    }

    @Test
    void exception_mono_1() {

        //given

        //when
        var mono = fluxAndMonoGeneratorService.exception_mono_exception();

        //then
        StepVerifier.create(mono)
                .expectErrorMessage("Exception Occurred")
                .verify();

    }

    @Test
    void exception_mono_onErrorResume() {

        //given
        var e = new IllegalStateException("Not a valid state");


        //when
        var mono = fluxAndMonoGeneratorService.exception_mono_onErrorResume(e);

        //then
        StepVerifier.create(mono)
                .expectNext("abc")
                .verifyComplete();
    }

    @Test
    void exception_mono_onErrorReturn() {

        //given


        //when
        var mono = fluxAndMonoGeneratorService.exception_mono_onErrorReturn();

        //then
        StepVerifier.create(mono)
                .expectNext("abc")
                .verifyComplete();
    }

    @Test
    void exception_mono_onErrorMap() {

        //given
        var e = new IllegalStateException("Not a valid state");


        //when
        var mono = fluxAndMonoGeneratorService.exception_mono_onErrorMap(e);

        //then
        StepVerifier.create(mono)
                .expectError(ReactorException.class)
                .verify();
    }

    @Test
    void exception_mono_onErrorContinue() {

        //given
        var input = "abc";

        //when
        var mono = fluxAndMonoGeneratorService.exception_mono_onErrorContinue(input);

        //then
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void exception_mono_onErrorContinue_1() {

        //given
        var input = "reactor";

        //when
        var mono = fluxAndMonoGeneratorService.exception_mono_onErrorContinue(input);

        //then
        StepVerifier.create(mono)
                .expectNext(input)
                .verifyComplete();
    }

    @Test
    void explore_generate() {

        //given
        //Demonstrate multiple emissions per round is not supported

        //when
        var flux = fluxAndMonoGeneratorService.explore_generate().log();

        //then
        StepVerifier.create(flux)
                .expectNext( 2, 4)
                .expectNextCount(8)
                .verifyComplete();

    }


    @Test
    void explore_create() {

        //given

        //when
        var flux = fluxAndMonoGeneratorService.explore_create().log();

        //then
        StepVerifier.create(flux)
                //.expectNext("alex", "ben", "chloe")
                .expectNextCount(6)
                .verifyComplete();

    }

    @Test
    void explore_create_mono() {

        //given

        //when
        var mono = fluxAndMonoGeneratorService.explore_create_mono().log();

        //then
        StepVerifier.create(mono)
                //.expectNext("alex", "ben", "chloe")
                .expectNext("alex")
                .verifyComplete();

    }

    @Test
    void explore_push() {

        //given

        //when
        var flux = fluxAndMonoGeneratorService.explore_push().log();

        //then
        StepVerifier.create(flux)
               //.expectNext("alex", "ben", "chloe")
                .expectNextCount(3)
               // .thenConsumeWhile(Objects::nonNull)
                .verifyComplete();

    }

    @Test
    void explore_handle() {

        //given

        //when
        var flux = fluxAndMonoGeneratorService.explore_handle().log();

        //then
        StepVerifier.create(flux)
                //.expectNext("alex", "ben", "chloe")
                .expectNextCount(2)
                .verifyComplete();

    }


    @Test
    void explore_mono_create() {

        //given

        //when
        var mono = fluxAndMonoGeneratorService.explore_mono_create();

        //then
        StepVerifier.create(mono)
                .expectNext("abc")
                .verifyComplete();

    }

    @Test
    void namesFlux_flatmap_sequential() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_sequential(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("A", "L", "E", "X")
                .expectNextCount(9)
                .verifyComplete();

    }


    @Test
    void namesFlux_delay() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_delay(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void range() {

        //given

        //when
        var rangeFlux = fluxAndMonoGeneratorService.range(5).log();

        //then
        StepVerifier.create(rangeFlux)
                //.expectNext(0,1,2,3,4)
                .expectNextCount(5)
                .verifyComplete();
    }
}
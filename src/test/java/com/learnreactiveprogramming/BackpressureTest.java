package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.learnreactiveprogramming.util.CommonUtil.delay;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class BackpressureTest {

    @Test
    public void testBackPressure() throws Exception {

        Flux<Integer> numberRange = Flux.range(1, 100).log();

        numberRange
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("hookOnNext : {}", value);
                        if (value == 2)
                            cancel();
                    }

                 /*   @Override
                    protected void hookOnNext(Integer value) {
                        // delay(1000);
                        System.out.println(value);
                        if (value %2 == 0 && value <100) {
                            request(2);
                        }else{
                            hookOnComplete();
                        }

                    }*/

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        log.error("Exception from upstream ", throwable);

                    }

                    @Override
                    protected void hookOnCancel() {
                        log.info("in hookOnCancel");
                    }

                    @Override
                    protected void hookOnComplete() {
                        log.info("in hookOnComplete");
                    }
                });
        //delay(5000);
    }

    @Test
    public void testBackPressure_1() throws Exception {

        Flux<Integer> numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        // delay(1000);
                        System.out.println(value);
                        if (value % 2 == 0 || value < 50) {
                            request(2);
                        } else {
                            cancel();
                            //hookOnCancel();
                        }

                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        throwable.printStackTrace();
                        //latch.countDown();
                    }

                    @Override
                    protected void hookOnCancel() {
                        //cancel();
                        latch.countDown();
                    }
                });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    /**
     * Publisher produces more data than the consumer
     * onBackpressureDrop operator requests for the unbounded demand and drops the elements that are not needed by the subscriber
     * Use this when you would like
     *
     * @throws Exception
     */
    @Test
    public void testBackPressure_drop() throws Exception {

        Flux<Integer> numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
                .onBackpressureDrop(x -> {
                    log.info("Dropped items are : {} ", x);
                }) // This operator does two things
                // 1. Makes the request as unbounded and stores the items in a queue
                // 2. drops the remaining elements that are not requested by the subscriber
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("Next Value is : {}", value);

                        if (value % 2 == 0 || value < 50) {
                            request(2);
                        } else {
                            //cancel();
                        }

                    }
                    @Override
                    protected void hookOnError(Throwable throwable) {
                        log.error("Exception is : ", throwable);
                        latch.countDown();
                    }

                    @Override
                    protected void hookOnCancel() {
                        latch.countDown();
                    }

                    @Override
                    protected void hookOnComplete() {
                        latch.countDown();
                    }
                });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    /**
     * onBackpressureBuffer operator requests for the unbounded demand
     * 1. Stores the values in an internal queue
     * 2. Pass the values to the subscriber when data is requested from the subscriber
     *
     * @throws Exception
     */
    @Test
    public void testBackPressure_buffer() throws Exception {

        Flux<Integer> numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
                .onBackpressureBuffer(10, BufferOverflowStrategy.ERROR)
                /*.onBackpressureBuffer(10, (i) -> {
                    log.info("Last Buffered element is : {}", i);
                })*/
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("Next Value is : {}", value);
                        if(value<50){
                            request(1);
                        }else{
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        log.error("Exception is : ", throwable);
                        latch.countDown();
                    }

                    @Override
                    protected void hookOnCancel() {
                        latch.countDown();
                    }

                    @Override
                    protected void hookOnComplete() {
                        latch.countDown();
                    }
                });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    /**
     * onBackpressureError operator requests for the unbounded demand
     * 1. Throws an Error if the publisher emits more data than requested
     *
     * @throws Exception
     */
    @Test
    public void testBackPressure_error() throws Exception {

        Flux<Integer> numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
                .onBackpressureError()
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("Next Value is : {}", value);
                        if(value<50){
                            request(1);
                        }else{
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        log.error("Exception is : ", throwable);
                        latch.countDown();
                    }

                    @Override
                    protected void hookOnCancel() {

                        latch.countDown();
                    }

                    @Override
                    protected void hookOnComplete() {
                        latch.countDown();
                    }
                });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

    /**
     * Need to figure this out
     *
     * @throws Exception
     */
    @Test
    public void testBackPressure_latest() throws Exception {

        Flux<Integer> numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        numberRange
                .onBackpressureLatest()
                .map(val -> {
                    log.info("Value is : {} ", val);
                    return val;
                })
                .subscribe(new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        delay(1000);
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("Next Value is : {}", value);
                        if (value % 2 == 0 && value < 20) {
                            delay(1000);
                        } else {
                            hookOnComplete();
                        }
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        log.error("Exception is : ", throwable);
                        latch.countDown();
                    }

                    @Override
                    protected void hookOnCancel() {
                        latch.countDown();
                    }

                    @Override
                    protected void hookOnComplete() {
                        latch.countDown();
                    }
                });
        assertTrue(latch.await(5L, TimeUnit.SECONDS));
    }

}

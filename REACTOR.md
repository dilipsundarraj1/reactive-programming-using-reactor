# A Reactor Sheet

## Section 8 : Transforming Flux and Mono

### 8.1 Transform using map() Operator
- Used to transform the element from one form to another in a Reactive Stream
- Similar to the map() operator in Streams API

Suppose the code
```java
public Flux<String> namesFlux_map(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .log();
    }
```
and the test case
```java
@Test
void namesFlux_map() {
        // given
        int stringLength = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLength);

        // then
        StepVerifier.create(namesFlux)
        .expectNext("ALEX", "BEN", "CHLOE")
        .verifyComplete();
}
```

### 8.2 Filter using filter() Operator
- Used to filter elements in a Reactive Stream
- Similar to the filter() operator in Streams API

Suppose the code
```java
public Flux<String> namesFlux_map(int stringLength) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
            .map(String::toUpperCase)
            .filter(s -> s.length()>stringLength)
            .map(s -> s.length() + "-"+s)
            .log();
}
```
and the test case
```java
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
```
with the following output
```java

18:57:44.063 [Test worker] INFO reactor.Flux.DoFinallyFuseable.1 - | onSubscribe([Fuseable] FluxDoFinally.DoFinallyFuseableSubscriber)
18:57:44.070 [Test worker] INFO reactor.Flux.DoFinallyFuseable.1 - | request(unbounded)
18:57:44.085 [Test worker] INFO reactor.Flux.DoFinallyFuseable.1 - | onNext(4-ALEX)
18:57:44.086 [Test worker] INFO reactor.Flux.DoFinallyFuseable.1 - | onNext(5-CHLOE)
18:57:44.086 [Test worker] INFO reactor.Flux.DoFinallyFuseable.1 - | onComplete()
```

### 8.3 Advanced transform using the flatMap()
- Transform one source element to a Flux of 1 to N elements
- Use it when the transformation return a Reactive Type (Flux or Mono)
- Return a Flux<Type>

Suppose the code
```java
public Flux<String> namesFlux_flatmap(int stringLength) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
    .map(String::toUpperCase)
    //.map(s -> s.toUpperCase())
    .filter(s -> s.length()>stringLength)
    .flatMap(s -> splitString(s))
    .log();
    }

private Flux<String> splitString(String name) {
  var charArray = name.split("");
  return Flux.fromArray(charArray);
}
```
and the test case
```java
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
```
with the following output
```java
19:09:07.694 [Test worker] INFO reactor.Flux.FlatMap.1 - onSubscribe(FluxFlatMap.FlatMapMain)
19:09:07.699 [Test worker] INFO reactor.Flux.FlatMap.1 - request(unbounded)
19:09:07.701 [Test worker] INFO reactor.Flux.FlatMap.1 - onNext(A)
19:09:07.702 [Test worker] INFO reactor.Flux.FlatMap.1 - onNext(L)
19:09:07.702 [Test worker] INFO reactor.Flux.FlatMap.1 - onNext(E)
19:09:07.702 [Test worker] INFO reactor.Flux.FlatMap.1 - onNext(X)
19:09:07.702 [Test worker] INFO reactor.Flux.FlatMap.1 - onNext(C)
19:09:07.702 [Test worker] INFO reactor.Flux.FlatMap.1 - onNext(H)
19:09:07.702 [Test worker] INFO reactor.Flux.FlatMap.1 - onNext(L)
19:09:07.703 [Test worker] INFO reactor.Flux.FlatMap.1 - onNext(O)
19:09:07.703 [Test worker] INFO reactor.Flux.FlatMap.1 - onNext(E)
19:09:07.703 [Test worker] INFO reactor.Flux.FlatMap.1 - onComplete()
```

### 8.4 Asynchronous Operations using flatMap()
Suppose the code
```java
    public Flux<String> namesFlux_flatmap_async(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        //.map(s -> s.toUpperCase())
        .filter(s -> s.length() > stringLength)
        .flatMap(s -> splitString_withDelay(s))
        .log();
        }

private Flux<String> splitString_withDelay(String name) {
        var charArray = name.split("");
        int delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
        .delayElements(Duration.ofMillis(delay));
        }
```
and the test case
```java
    @Test
    void namesFlux_flatmap_async() {
        // given
        int stringLength = 3;

        // when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_flatmap_async(stringLength);

        // then
        StepVerifier.create(namesFlux)
                //we can't expect the sequence to be ordered like ("A","L","E","X","C","H","L","O","E")
                .expectNextCount(9)
                .verifyComplete();
    }
```
with the following output
```java
19:15:21.283 [Test worker] INFO reactor.Flux.FlatMap.1 - onSubscribe(FluxFlatMap.FlatMapMain)
19:15:21.287 [Test worker] INFO reactor.Flux.FlatMap.1 - request(unbounded)
19:15:21.493 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(C)
19:15:21.648 [parallel-3] INFO reactor.Flux.FlatMap.1 - onNext(H)
19:15:21.804 [parallel-4] INFO reactor.Flux.FlatMap.1 - onNext(L)
19:15:21.866 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(A)
19:15:21.959 [parallel-5] INFO reactor.Flux.FlatMap.1 - onNext(O)
19:15:22.112 [parallel-7] INFO reactor.Flux.FlatMap.1 - onNext(E)
19:15:22.396 [parallel-6] INFO reactor.Flux.FlatMap.1 - onNext(L)
19:15:22.922 [parallel-8] INFO reactor.Flux.FlatMap.1 - onNext(E)
19:15:23.455 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(X)
19:15:23.456 [parallel-1] INFO reactor.Flux.FlatMap.1 - onComplete()
```

| map()                                                  | Flatmap()                                                                                                                                           |
|--------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| One to One Tranformation                               | One to N transformations                                                                                                                            |
| Does the simple transformation from T to V             | Does more than just transformation. Subscribes to Flux or Mono that's part of the <br/> transformation and then flattens it and sends ot downstream |
| Used for simple synchronous transformations            | Used for asynchronous transformations                                                                                                               |
| Does not support transformations that return Publisher | use it with transformations that returns Publisher                                                                                                  |


### 8.5 Advanced transform using the concatMap()
- Works similar to flatMap()
- Only difference is that *concatMap* preserves the ordering sequence of the Reactive Streams
- it takes more time that flatmap() to complete
- Use *concatMap()* if ordering matters

Suppose the code
```java
    public Flux<String> namesFlux_concatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                //.map(s -> s.toUpperCase())
                .filter(s -> s.length() > stringLength)
                .concatMap(s -> splitString_withDelay(s))
                .log();
    }

    private Flux<String> splitString_withDelay(String name) {
        var charArray = name.split("");
        var delay = 1000
        return Flux.fromArray(charArray)
        .delayElements(Duration.ofMillis(delay));
        }
```
and the test case
```java
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
```
with the following output - the test take exactly 9s
```java
19:33:39.777 [Test worker] INFO reactor.Flux.ConcatMap.1 - onSubscribe(FluxConcatMap.ConcatMapImmediate)
19:33:39.783 [Test worker] INFO reactor.Flux.ConcatMap.1 - request(unbounded)
19:33:40.835 [parallel-1] INFO reactor.Flux.ConcatMap.1 - onNext(A)
19:33:41.847 [parallel-2] INFO reactor.Flux.ConcatMap.1 - onNext(L)
19:33:42.859 [parallel-3] INFO reactor.Flux.ConcatMap.1 - onNext(E)
19:33:43.872 [parallel-4] INFO reactor.Flux.ConcatMap.1 - onNext(X)
19:33:44.883 [parallel-5] INFO reactor.Flux.ConcatMap.1 - onNext(C)
19:33:45.884 [parallel-6] INFO reactor.Flux.ConcatMap.1 - onNext(H)
19:33:46.888 [parallel-7] INFO reactor.Flux.ConcatMap.1 - onNext(L)
19:33:47.897 [parallel-8] INFO reactor.Flux.ConcatMap.1 - onNext(O)
19:33:48.902 [parallel-1] INFO reactor.Flux.ConcatMap.1 - onNext(E)
19:33:48.902 [parallel-1] INFO reactor.Flux.ConcatMap.1 - onComplete()
```

### 8.6 flatMap() operator in Mono
- Use it when the transformation returns a Mono
- Return a Mono<T>
- Use flatMap if the transformation involves making a REST API call or any king of functionality that can be done asynchronously

Suppose the code
```java
    public Mono<List<String>> namesMono_flatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray);
        return Mono.just(charList);
    }
```
and the test case
```java
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
```
with the following output
```java
01:29:32.578 [Test worker] INFO reactor.Mono.FlatMap.1 - | onSubscribe([Fuseable] MonoFlatMap.FlatMapMain)
01:29:32.586 [Test worker] INFO reactor.Mono.FlatMap.1 - | request(unbounded)
01:29:32.591 [Test worker] INFO reactor.Mono.FlatMap.1 - | onNext([A, L, E, X])
01:29:32.592 [Test worker] INFO reactor.Mono.FlatMap.1 - | onComplete()
```

### 8.7 flatMapMany() operator in Mono
- Works very similar to flatMap()

Suppose the code
```java
    public Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }
```
and the test case
```java
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
```
with the following output
```java
01:32:32.319 [Test worker] INFO reactor.Flux.MonoFlatMapMany.1 - onSubscribe(MonoFlatMapMany.FlatMapManyMain)
01:32:32.322 [Test worker] INFO reactor.Flux.MonoFlatMapMany.1 - request(unbounded)
01:32:32.324 [Test worker] INFO reactor.Flux.MonoFlatMapMany.1 - onNext(A)
01:32:32.324 [Test worker] INFO reactor.Flux.MonoFlatMapMany.1 - onNext(L)
01:32:32.325 [Test worker] INFO reactor.Flux.MonoFlatMapMany.1 - onNext(E)
01:32:32.325 [Test worker] INFO reactor.Flux.MonoFlatMapMany.1 - onNext(X)
01:32:32.325 [Test worker] INFO reactor.Flux.MonoFlatMapMany.1 - onComplete()
```

### 8.8 Transform using the transform() Operator
- Used to transform from one type to another
- Accepts *Function Functional Interface*
  - Function Functional Interface got released as part of Java 8
  - Input - Publisher (Flux or Mono)
  - Output - Publisher (Flux or Mono)
- Used to extract a logic that can be reused

Suppose the code
```java
    public Flux<String> namesFlux_transform(int stringLength) {

        // we can reuse the functionality
        Function<Flux<String>, Flux<String>> filtermap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filtermap)
                .flatMap(s -> splitString(s))
                .log();
    }
```
and the test case
```java
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
```
with the following output
```java
01:39:49.661 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onSubscribe([Fuseable] FluxDefaultIfEmpty.DefaultIfEmptySubscriber)
01:39:49.665 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - request(unbounded)
01:39:49.667 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(A)
01:39:49.667 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(L)
01:39:49.667 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(E)
01:39:49.668 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(X)
01:39:49.668 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(C)
01:39:49.668 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(H)
01:39:49.668 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(L)
01:39:49.668 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(O)
01:39:49.668 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(E)
01:39:49.669 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onComplete()
```

### 8.9 Handling empty data using defaultIfEmpty() and swithIfEmpty() Operators
- Its not mandatory for a data source to emit data all the time
- We can use those operators to return a default value

Suppose the code
```java
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
```
and the test case
```java
    @Test
    void namesFlux_transform_1() {
        // given
        int stringLength = 6; // No values have a length of 6

        // when
        Flux<String> namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        // then
        StepVerifier.create(namesFlux)
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
```
with the following output
```java
01:44:44.825 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onSubscribe([Fuseable] FluxDefaultIfEmpty.DefaultIfEmptySubscriber)
01:44:44.830 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - request(unbounded)
01:44:44.831 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onNext(default)
01:44:44.832 [Test worker] INFO reactor.Flux.DefaultIfEmpty.1 - onComplete()
        
-----------

01:48:12.287 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - onSubscribe(FluxSwitchIfEmpty.SwitchIfEmptySubscriber)
01:48:12.292 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - request(unbounded)
01:48:12.298 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - onNext(D)
01:48:12.298 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - onNext(E)
01:48:12.298 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - onNext(F)
01:48:12.299 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - onNext(A)
01:48:12.299 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - onNext(U)
01:48:12.299 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - onNext(L)
01:48:12.299 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - onNext(T)
01:48:12.299 [Test worker] INFO reactor.Flux.SwitchIfEmpty.1 - onComplete()
```

## Section 9 : Combining Flux and Mono

### 9.1 concat() & concatWith()
- Used to combine two reactive streams in to one
- Concatenation of Reactive Streams happens in a sequence
  - First one is subscribed first and competed
  - Second one in subscribed after that and then completes
- *concat()* - static method in Flux
- *concatWith()* - Instance method in Flux and Mono
- Both of thse operator works similarly

Suppose the code
```java
    public Flux<String> explore_concat() {

        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return Flux.concat(abcFlux, defFlux);
    }

    public Flux<String> explore_concatwith() {

        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");

        return abcFlux.concatWith(defFlux);
    }

    public Flux<String> explore_concatwith_mono() {

        var aMono = Mono.just("A");
        var bMono = Flux.just("B");

        return aMono.concatWith(bMono);
    }
```
and the test case
```java
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
```
with the following output
```java
01:56:59.484 [Test worker] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
01:56:59.489 [Test worker] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
01:56:59.492 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(A)
01:56:59.493 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(B)
01:56:59.493 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(C)
01:56:59.494 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(D)
01:56:59.495 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(E)
01:56:59.496 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(F)
01:56:59.496 [Test worker] INFO reactor.Flux.ConcatArray.1 - onComplete()
```

### 9.2 Combining Reactive Streams using merge() & mergeWith()
- merge() operator take in two arguments
- Both the publishers are subscribed at the same time
  - Publishers are subscribed eagerly and the merge happens in an interleaved fashion
  - concat() subscribes to the Publishers in a sequence

Suppose the code
```java
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
```
and the test case
```java
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
```
with the following output
```java
02:04:26.675 [Test worker] INFO reactor.Flux.Merge.1 - onSubscribe(FluxFlatMap.FlatMapMain)
02:04:26.681 [Test worker] INFO reactor.Flux.Merge.1 - request(unbounded)
02:04:26.834 [parallel-1] INFO reactor.Flux.Merge.1 - onNext(A)
02:04:26.848 [parallel-2] INFO reactor.Flux.Merge.1 - onNext(D)
02:04:26.941 [parallel-3] INFO reactor.Flux.Merge.1 - onNext(B)
02:04:26.987 [parallel-4] INFO reactor.Flux.Merge.1 - onNext(E)
02:04:27.049 [parallel-5] INFO reactor.Flux.Merge.1 - onNext(C)
02:04:27.127 [parallel-6] INFO reactor.Flux.Merge.1 - onNext(F)
02:04:27.128 [parallel-6] INFO reactor.Flux.Merge.1 - onComplete()

---------

02:07:23.215 [Test worker] INFO reactor.Flux.Merge.1 - onSubscribe(FluxFlatMap.FlatMapMain)
02:07:23.220 [Test worker] INFO reactor.Flux.Merge.1 - request(unbounded)
02:07:23.220 [Test worker] INFO reactor.Flux.Merge.1 - onNext(A)
02:07:23.221 [Test worker] INFO reactor.Flux.Merge.1 - onNext(B)
02:07:23.221 [Test worker] INFO reactor.Flux.Merge.1 - onComplete()
```

### 9.3 Combining Reactive Streams using mergeSequential()
- Used to combine two Publishers (Flux) in to one
- Static method in Flux
- Both the publishers are subscribed at the same tine
  - Publishers are subscribed eagerly
  - Evn though the publishers ans subscribed eagerly the merge happens in a sequence:

Suppose the code
```java
    public Flux<String> explore_mergeSequential() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(125));

        return Flux.mergeSequential(abcFlux, defFlux).log();
    }
```
and the test case
```java
    @Test
    void explore_mergeSequential() {

        var mergeSequential = fluxAndMonoGeneratorService.explore_mergeSequential();

        StepVerifier.create(mergeSequential)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }
```
with the following output
```java
02:12:31.656 [Test worker] INFO reactor.Flux.MergeSequential.1 - onSubscribe(FluxMergeSequential.MergeSequentialMain)
02:12:31.660 [Test worker] INFO reactor.Flux.MergeSequential.1 - request(unbounded)
02:12:31.798 [parallel-1] INFO reactor.Flux.MergeSequential.1 - onNext(A)
02:12:31.906 [parallel-3] INFO reactor.Flux.MergeSequential.1 - onNext(B)
02:12:32.015 [parallel-5] INFO reactor.Flux.MergeSequential.1 - onNext(C)
02:12:32.015 [parallel-5] INFO reactor.Flux.MergeSequential.1 - onNext(D)
02:12:32.015 [parallel-5] INFO reactor.Flux.MergeSequential.1 - onNext(E)
02:12:32.107 [parallel-6] INFO reactor.Flux.MergeSequential.1 - onNext(F)
02:12:32.107 [parallel-6] INFO reactor.Flux.MergeSequential.1 - onComplete()
```

### 9.14 Combining Reactive Streams using zip() and zipWith() Operator
- Zips two publishers together
- Waits for all the publishers involved in the transformation to emit one element
  - Continues until one publisher sends an OnComplete event
- Can be used to merge up-to 2 to 8 Publishers in to one

Suppose the code
```java
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
```
and the test case
```java
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
```
with the following output
```java
02:21:07.583 [Test worker] INFO reactor.Flux.Zip.1 - onSubscribe(FluxZip.ZipCoordinator)
02:21:07.588 [Test worker] INFO reactor.Flux.Zip.1 - request(unbounded)
02:21:07.591 [Test worker] INFO reactor.Flux.Zip.1 - onNext(AD)
02:21:07.591 [Test worker] INFO reactor.Flux.Zip.1 - onNext(BE)
02:21:07.591 [Test worker] INFO reactor.Flux.Zip.1 - onNext(CF)
02:21:07.592 [Test worker] INFO reactor.Flux.Zip.1 - onComplete()


02:21:24.634 [Test worker] INFO reactor.Flux.Map.1 - onSubscribe(FluxMap.MapSubscriber)
02:21:24.638 [Test worker] INFO reactor.Flux.Map.1 - request(unbounded)
02:21:24.654 [Test worker] INFO reactor.Flux.Map.1 - onNext(AD14)
02:21:24.655 [Test worker] INFO reactor.Flux.Map.1 - onNext(BE25)
02:21:24.655 [Test worker] INFO reactor.Flux.Map.1 - onNext(CF36)
02:21:24.656 [Test worker] INFO reactor.Flux.Map.1 - onComplete()

        
02:21:43.151 [Test worker] INFO reactor.Flux.Zip.1 - onSubscribe(FluxZip.ZipCoordinator)
02:21:43.155 [Test worker] INFO reactor.Flux.Zip.1 - request(unbounded)
02:21:43.157 [Test worker] INFO reactor.Flux.Zip.1 - onNext(AD)
02:21:43.157 [Test worker] INFO reactor.Flux.Zip.1 - onNext(BE)
02:21:43.157 [Test worker] INFO reactor.Flux.Zip.1 - onNext(CF)
02:21:43.157 [Test worker] INFO reactor.Flux.Zip.1 - onComplete()


02:22:06.565 [Test worker] INFO reactor.Mono.Map.1 - onSubscribe(FluxMap.MapSubscriber)
02:22:06.570 [Test worker] INFO reactor.Mono.Map.1 - request(unbounded)
02:22:06.572 [Test worker] INFO reactor.Mono.Map.1 - onNext(AB)
02:22:06.573 [Test worker] INFO reactor.Mono.Map.1 - onComplete()
```


## Section 11 : DoOn* Callbacks - Peeking into a sequence

### DoOn* CallBacks

- These operators allow you to peek in to the events that are emitted by the Publisher (Flux or Mono)
- These are also called side effects operators
  -- They don't change the original sequence at all

### Operators

|DoOn Callback Functions           |Usage                        |
|----------------------------------|-----------------------------|
|**doOnSubscribe()**                   |Invoked for every new subscription from the Subscriber|
|**doOnNext()**                        |Invoked for every element that's emitted from the publisher|
|**doOnComplete()**                    |Invoked when the completion signal is sent from the publisher|
|**doOnError()**                       |Invoked when an exception signal is sent from the publisher|
|**doFinally()**                       |Invoked in a successful or error scenario|

### When to use doOn* CallBack operators ?

- Used for debugging an issue in your local environment
- Send a notification when the reactive sequence completes or error out


## Section 12 : Exception/Error handling in Flux and Mono

### 12.1 Exceptions in Reactive Streams

An example could be a call to an external service from your App

```mermaid
graph LR
A[App] -- X --> B[External Service]
```
Suppose the function
```java
public Flux<String> exception_flux() {  
  
    return Flux.just("A", "B", "C")  
            .concatWith(Flux.error(new RuntimeException("Exception Occured")))  
            .concatWith(Flux.just("D"))  
            .log();  
}
```

And the following test case
```java
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
```

From the tests cases we can see that the error stop the stream and we will never received the "D" element.
```java
16:44:18.571 [Test worker] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
16:44:18.575 [Test worker] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
16:44:18.576 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(A)
16:44:18.576 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(B)
16:44:18.576 [Test worker] INFO reactor.Flux.ConcatArray.1 - onNext(C)
16:44:18.578 [Test worker] ERROR reactor.Flux.ConcatArray.1 - onError(java.lang.RuntimeException: Exception Occured)
16:44:18.580 [Test worker] ERROR reactor.Flux.ConcatArray.1 - 
java.lang.RuntimeException: Exception Occured
	at com.learnreactiveprogramming.service.FluxAndMonoGeneratorService.exception_flux(FluxAndMonoGeneratorService.java:261)
	at com.learnreactiveprogramming.service.FluxAndMonoGeneratorServiceTest.exception_flux(FluxAndMonoGeneratorServiceTest.java:329)
```

### 12.2 Exceptions handling

- Two categories of Operators :
    - **Category 1** : Recover from an Exception
    - **Category 2** : Take an action on the exception and re-throw the exception


|Recover From an Exception         |Take an Action and throw the Exception                        |
|----------------------------------|-----------------------------|
|**onErrorReturn()**               |**OnErrorMap()**|
|**onErrorResume()**               |**doOnError()**|
|**onErrorContinue()**             ||

### 12.3 onErrorReturn() : Exception Handling Operator
- Catch the exception
- This also provides a single default value as a fallback value

Suppose the function
```java
public Flux<String> explore_OnErrorReturn() {  
  
    return Flux.just("A", "B", "C")  
            .concatWith(Flux.error(new IllegalStateException("Exception Occured")))  
            .onErrorReturn("D")  
            .log();  
}
```
And the test case
```java
@Test  
void explore_OnErrorReturn() {  
    var value = fluxAndMonoGeneratorService.explore_OnErrorReturn();  
  
    StepVerifier.create(value)  
            .expectNext("A", "B", "C", "D")  
            .verifyComplete();  
}
```
with the following output
```java
17:10:46.080 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onSubscribe(FluxOnErrorResume.ResumeSubscriber)
17:10:46.083 [Test worker] INFO reactor.Flux.OnErrorResume.1 - request(unbounded)
17:10:46.084 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(A)
17:10:46.084 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(B)
17:10:46.084 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(C)
17:10:46.085 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(D)
17:10:46.086 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onComplete()
BUILD SUCCESSFUL in 2s
```

### 12.4 onErrorResume() : Exception Handling Operator
- Catch the exception
- This provide a fallback stream as a recoverable value

Suppose the function
```java
public Flux<String> explore_OnErrorResume(Exception e) {  
  
    var recoveryFlux = Flux.just("D", "E", "F");  
  
    return Flux.just("A", "B", "C")  
            .concatWith(Flux.error(e))  
            .onErrorResume(ex -> {  
                log.error("Exception is ", ex);  
                return recoveryFlux;  
            })  
            .log();  
}
```
And the test case
```java
@Test  
void explore_OnErrorResume() {  
    var e = new IllegalStateException("Not a valid State");  
  
    var value = fluxAndMonoGeneratorService.explore_OnErrorResume(e);  
  
    StepVerifier.create(value)  
            .expectNext("A", "B", "C", "D", "E", "F")  
            .verifyComplete();  
}
```

With the following output
```java
17:51:57.822 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onSubscribe(FluxOnErrorResume.ResumeSubscriber)
17:51:57.825 [Test worker] INFO reactor.Flux.OnErrorResume.1 - request(unbounded)
17:51:57.826 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(A)
17:51:57.826 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(B)
17:51:57.826 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(C)
17:51:57.828 [Test worker] ERROR com.learnreactiveprogramming.service.FluxAndMonoGeneratorService - Exception is 
java.lang.IllegalAccessException: Exception occurred
	at com.learnreactiveprogramming.service.FluxAndMonoGeneratorServiceTest.explore_OnErrorResume(FluxAndMonoGeneratorServiceTest.java:368)
	18:00:54.260 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(D)
18:00:54.261 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(E)
18:00:54.262 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(F)
18:00:54.263 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onComplete()
```
#### Conditional recovery
Suppose the function
```java
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
```
And the test case
```java
@Test  
void explore_OnErrorResume_1() {  
    var e = new RuntimeException("Not a valid State");  
  
    var value = fluxAndMonoGeneratorService.explore_OnErrorResume(e);  
  
    StepVerifier.create(value)  
            .expectNext("A", "B", "C")  
            .expectError(RuntimeException.class)  
            .verify();  
}
```
with the following output
```java
18:06:28.905 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onSubscribe(FluxOnErrorResume.ResumeSubscriber)
18:06:28.907 [Test worker] INFO reactor.Flux.OnErrorResume.1 - request(unbounded)
18:06:28.909 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(A)
18:06:28.909 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(B)
18:06:28.909 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(C)
18:06:28.913 [Test worker] ERROR com.learnreactiveprogramming.service.FluxAndMonoGeneratorService - Exception is 
java.lang.RuntimeException: Not a valid State
	at com.learnreactiveprogramming.service.FluxAndMonoGeneratorServiceTest.explore_OnErrorResume_1(FluxAndMonoGeneratorServiceTest.java:379)
```

### 12.5 onErrorContinue() : Exception Handling Operator
- Catches the exception
- This drop the elements that cause the exception and continue emitting the remaining elements

Suppose the function
```java
public Flux<String> explore_OnErrorContinue() {  
  
    return Flux.just("A", "B", "C")  
            .map(name -> {  
                if(name.equals("B"))  
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
```
and the test case
```java
@Test  
void explore_OnErrorContinue() {  
    var value = fluxAndMonoGeneratorService.explore_OnErrorContinue();  
  
    StepVerifier.create(value)  
            .expectNext("A", "C", "D")  
            .verifyComplete();  
}
```
with the following output
```java
21:34:17.841 [Test worker] INFO reactor.Flux.ContextWrite.1 - | onSubscribe([Fuseable] FluxContextWrite.ContextWriteSubscriber)
21:34:17.845 [Test worker] INFO reactor.Flux.ContextWrite.1 - | request(unbounded)
21:34:17.847 [Test worker] INFO reactor.Flux.ContextWrite.1 - | onNext(A)
21:34:17.851 [Test worker] ERROR com.learnreactiveprogramming.service.FluxAndMonoGeneratorService - Exception is 
java.lang.IllegalStateException: Exception Occurred
	at com.learnreactiveprogramming.service.FluxAndMonoGeneratorService.lambda$explore_OnErrorContinue$27(FluxAndMonoGeneratorService.java:298)
21:34:17.857 [Test worker] INFO com.learnreactiveprogramming.service.FluxAndMonoGeneratorService - name is B
21:34:17.858 [Test worker] INFO reactor.Flux.ContextWrite.1 - | onNext(C)
21:34:17.858 [Test worker] INFO reactor.Flux.ContextWrite.1 - | onNext(D)
21:34:17.859 [Test worker] INFO reactor.Flux.ContextWrite.1 - | onComplete()
```

### 12.6 onErrorMap() : Exception Handling Operator
- Catches the exception
- Transforms the exception from one type to another
    - Any RuntimeException to BusinessException
- Does not recover from the exception

Suppose the function
```java
public Flux<String> explore_OnErrorMap() {  
  
    return Flux.just("A", "B", "C")  
            .map(name -> {  
                if(name.equals("B"))  
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
```
and the test case
```java
@Test  
void explore_OnErrorMap() {  
    var value = fluxAndMonoGeneratorService.explore_OnErrorMap();  
  
    StepVerifier.create(value)  
            .expectNext("A")  
            .expectError(ReactorException.class)  
            .verify();  
}
```
with the following output
```java
21:48:12.589 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onSubscribe(FluxOnErrorResume.ResumeSubscriber)
21:48:12.594 [Test worker] INFO reactor.Flux.OnErrorResume.1 - request(unbounded)
21:48:12.597 [Test worker] INFO reactor.Flux.OnErrorResume.1 - onNext(A)
21:48:12.601 [Test worker] ERROR com.learnreactiveprogramming.service.FluxAndMonoGeneratorService - Exception is 
java.lang.IllegalStateException: Exception Occurred
	at com.learnreactiveprogramming.service.FluxAndMonoGeneratorService.lambda$explore_OnErrorMap$29(FluxAndMonoGeneratorService.java:315)
21:48:12.639 [Test worker] ERROR reactor.Flux.OnErrorResume.1 - onError(com.learnreactiveprogramming.exception.ReactorException)
21:48:12.640 [Test worker] ERROR reactor.Flux.OnErrorResume.1 - 
com.learnreactiveprogramming.exception.ReactorException: null
	at com.learnreactiveprogramming.service.FluxAndMonoGeneratorService.lambda$explore_OnErrorMap$30(FluxAndMonoGeneratorService.java:321)
```

### 12.7 doOnError() : Catching Exceptions and Throw the error
- Catches the exception
- Take an action when an Exception occurs in the pipeline
- Does not modify the Reactive Stream
    - Error still gets propagated to the caller

Suppose the function
```java
public Flux<String> explore_doOnError() {  
  
    return Flux.just("A", "B", "C")  
            .concatWith(Flux.error(new IllegalStateException("Exception Occured")))  
            .doOnError(ex -> {  
                log.error("Exception is ", ex);  
            })  
            .log();  
}
```
and the test case
```java
@Test  
void explore_doOnError() {  
    var value = fluxAndMonoGeneratorService.explore_doOnError();  
  
    StepVerifier.create(value)  
            .expectNext("A", "B", "C")  
            .expectError(IllegalStateException.class)  
            .verify();  
}
```
with the following output
```java
22:04:58.239 [Test worker] INFO reactor.Flux.Peek.1 - onSubscribe(FluxPeek.PeekSubscriber)
22:04:58.243 [Test worker] INFO reactor.Flux.Peek.1 - request(unbounded)
22:04:58.245 [Test worker] INFO reactor.Flux.Peek.1 - onNext(A)
22:04:58.246 [Test worker] INFO reactor.Flux.Peek.1 - onNext(B)
22:04:58.246 [Test worker] INFO reactor.Flux.Peek.1 - onNext(C)
22:04:58.250 [Test worker] ERROR com.learnreactiveprogramming.service.FluxAndMonoGeneratorService - Exception is 
java.lang.IllegalStateException: Exception Occured
	at com.learnreactiveprogramming.service.FluxAndMonoGeneratorService.explore_doOnError(FluxAndMonoGeneratorService.java:328)
```

### 12.8 Exception Handling Operators in Mono
Mono has the support for all the exception handling operators that we coded until now for Flux

Suppose the function
```java
public Mono<Object> explore_Mono_OnErrorReturn() {  
  
    return Mono.just("A")  
            .map(value -> {  
                throw new RuntimeException("Exception Occured");  
            })  
            .onErrorReturn("abc")  
            .log();  
}
```
and the test case
```java
@Test  
void explore_Mono_OnErrorReturn() {  
    var value = fluxAndMonoGeneratorService.explore_Mono_OnErrorReturn();  
  
    StepVerifier.create(value)  
            .expectNext("abc")  
            .verifyComplete();  
}
```
with the following output
```java
22:14:57.065 [Test worker] INFO reactor.Mono.OnErrorResume.1 - onSubscribe(FluxOnErrorResume.ResumeSubscriber)
22:14:57.068 [Test worker] INFO reactor.Mono.OnErrorResume.1 - request(unbounded)
22:14:57.071 [Test worker] INFO reactor.Mono.OnErrorResume.1 - onNext(abc)
22:14:57.072 [Test worker] INFO reactor.Mono.OnErrorResume.1 - onComplete()
BUILD SUCCESSFUL in 3s
```

## Section 13: Implement Exception Handling in a Service

Suppose the service
```java
public Flux<Movie> getAllMovies() {  
  
    var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();  
    return moviesInfoFlux  
			.flatMap(movieInfo -> {  
			    var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())  
			            .collectList();  
			    return reviewsMono  
						.map(reviewsList -> new Movie(movieInfo, reviewsList));  
			 })  
			 .onErrorMap((ex) -> {  
			     log.error("Exception is : ", ex);  
			     throw new MovieException(ex.getMessage());  
			 })  
			 .log();  
}
```
and the test case
```java
@Test  
void getAllMovies() {  
    Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();  
    Mockito.when(reviewService.retrieveReviewsFlux(anyLong())).thenCallRealMethod();  
  
    var moviesFlux = movieReactiveService.getAllMovies();  
  
    StepVerifier.create(moviesFlux)  
            .expectNextCount(3)  
            .verifyComplete();  
}

@Test  
void getAllMovies_1() {  
    var errorMessage = "Exception occurred in ReviewService";  
    Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();  
  
    Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))  
            .thenThrow(new RuntimeException(errorMessage));  
  
    var moviesFlux = movieReactiveService.getAllMovies();  
  
    StepVerifier.create(moviesFlux)  
            .expectError(MovieException.class)  
            .verify();  
}
```

## Section 14: Retry, Repeat using retry(), retryWhen(), repeat()

### 14.1 Retry exception using retry() and retry(n)
- Use this operator to retry failed exceptions
- When to use it ?
  - Code interacts with external systems through network
  - These calls may fail 

#### Retry()
- **Retry()**
  - Retry the failed exception indefinitely
- **Retry(n)**
  - N is a long value
  - Retry the failed exception "n" number of times

Suppose the code
```java
public Flux<Movie> getAllMovies_retry() {

        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap((ex) -> {
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                })
                .retry(3)
                .log();
    }
```
and the test case
```java
@Test
    void getAllMovies_retry() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMovies_retry();

        StepVerifier.create(moviesFlux)
                .expectError(MovieException.class)
                .verify();

        Mockito.verify(reviewService, Mockito.times(4)).retrieveReviewsFlux(isA(Long.class));
    }
```

### 14.2 Retry specific Exception using retryWhen()
- **retryWhen()** is more advanced compared to retry()
- Conditionally perform retry on specific exceptions

Suppose the code
```java
public Flux<Movie> getAllMovies_retryWhen() {

        var retryWhen = Retry.backoff(3, Duration.ofMillis(500))
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure()));

        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap((ex) -> {
                    log.error("Exception is : ", ex);
                    throw new MovieException(ex.getMessage());
                })
                .retryWhen(retryWhen)
                .log();
    }
```
and the test case
```java
@Test
    void getAllMovies_retryWhen() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new RuntimeException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMovies_retryWhen();

        StepVerifier.create(moviesFlux)
                .expectError(MovieException.class)
                .verify();

        Mockito.verify(reviewService, Mockito.times(4)).retrieveReviewsFlux(isA(Long.class));
    }
```

Know suppose you want to retry just after specific exceptions
```java
public Flux<Movie> getAllMovies_retryWhen() {

        var retryWhen = Retry.backoff(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof MovieException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure()));

        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap((ex) -> {
                    log.error("Exception is : ", ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(retryWhen)
                .log();
    }
```
and the test case
```java
@Test
    void getAllMovies_retryWhen_1() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenThrow(new ServiceException(errorMessage));

        var moviesFlux = movieReactiveService.getAllMovies_retryWhen();

        StepVerifier.create(moviesFlux)
                .expectErrorMessage(errorMessage)
                .verify();

        Mockito.verify(reviewService, Mockito.times(1)).retrieveReviewsFlux(isA(Long.class));
    }
```

### 14.3 Repeat a Sequence using repeat() and repeat(n)
- Used to repeat an existing sequence
- This operator gets invoked after the onCompletion() event from the existing sequence
- Use it when you have an use-case to subscribe to same publisher again
- This operator works as long as No Exception is thrown

**repeat()**
- Subscribes to the publisher indefinitely

**repeat(n)**
- Subscribes to the publisher "N" times

Suppose the code
```java
public Flux<Movie> getAllMovies_repeat() {

        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap((ex) -> {
                    log.error("Exception is : ", ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .repeat()
                .log();
    }
```
and the test case
```java
@Test
    void getAllMovies_repeat() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();

        var moviesFlux = movieReactiveService.getAllMovies_repeat();

        StepVerifier.create(moviesFlux)
                .expectNextCount(6)
                .thenCancel()
                .verify();

        Mockito.verify(reviewService, Mockito.times(6)).retrieveReviewsFlux(isA(Long.class));
    }
```

Suppose the other code with repeat(n)
```java
public Flux<Movie> getAllMovies_repeat_n(long n) {

        var moviesInfoFlux = movieInfoService.retrieveMoviesFlux();
        return moviesInfoFlux
                .flatMap(movieInfo -> {
                    var reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewsMono
                            .map(reviewsList -> new Movie(movieInfo, reviewsList));
                })
                .onErrorMap((ex) -> {
                    log.error("Exception is : ", ex);
                    if (ex instanceof NetworkException)
                        throw new MovieException(ex.getMessage());
                    else
                        throw new ServiceException(ex.getMessage());
                })
                .retryWhen(getRetryBackoffSpec())
                .repeat(n)
                .log();
    }
```
and the test case
```java
@Test
    void getAllMovies_repeat_n() {
        var errorMessage = "Exception occurred in ReviewService";
        Mockito.when(movieInfoService.retrieveMoviesFlux())
                .thenCallRealMethod();

        Mockito.when(reviewService.retrieveReviewsFlux(anyLong()))
                .thenCallRealMethod();
        var noOfTimes = 2L;

        var moviesFlux = movieReactiveService.getAllMovies_repeat_n(noOfTimes);

        StepVerifier.create(moviesFlux)
                .expectNextCount(9)
                .verifyComplete();

        Mockito.verify(reviewService, Mockito.times(9)).retrieveReviewsFlux(isA(Long.class));
    }
```

## Section 15: Reactors Execution Model - Schedulers, Threads and Threadpool

**Scheduler Options**
- **Schedulers** is a factory class that can be used to switch the threads in the reactive pipeline execution
- **Schedulers.parallel()**
  - It has a fixed pool of workers. No of threads is to no of CPU cores
  - The time based operators use this by default(delayElements(), interval())
- **Schedulers.boundElastic()**
  - It has a bounded elastic thread pool of workers
  - The no of threads can grow based on the need. It can increase up to 10 X no of CPU cores
  - This is odeal for makin Blocking IO calls
- **Schedulers.single()**
  - A single reusable thread for executing the tasks

### 15.1: Switching Threads using publishOn()
- This operator is used to hop the Thread of execution of the reactive pipeline from one to another
- When to use **publishOn(Scheduler s)**?
  - Never block the thread in reactive programming
  - Blocking operation in the reactive pipeline can be performed after **publisOn** operator
  - The thread of execution is determined by the Scheduler that passed to it
- publishOn() is used to influence the thread downstream()

Suppose the code without publishOn
```java
    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    public Flux<String> explore_publishOn(){

        var namesFlux = Flux.fromIterable(namesList)
                .map(this::upperCase)
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .map(this::upperCase)
                .log();
       return namesFlux.mergeWith(namesFlux1);
    }

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }
```
and the test case
```java
@Test
    void explore_publishOn() {

        var flux = fluxAndMonoSchedulersService.explore_publishOn();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }
```
with the following output, you can see a single thread and an ordered sequence. This is due too the **delay(1000)**
```java
23:48:12.664 [Test worker] INFO reactor.Flux.MapFuseable.1 - | onSubscribe([Fuseable] FluxMapFuseable.MapFuseableSubscriber)
        23:48:12.667 [Test worker] INFO reactor.Flux.MapFuseable.1 - | request(32)
        23:48:13.678 [Test worker] INFO reactor.Flux.MapFuseable.1 - | onNext(ALEX)
        23:48:14.688 [Test worker] INFO reactor.Flux.MapFuseable.1 - | onNext(BEN)
        23:48:15.691 [Test worker] INFO reactor.Flux.MapFuseable.1 - | onNext(CHLOE)
        23:48:15.692 [Test worker] INFO reactor.Flux.MapFuseable.1 - | onComplete()
        23:48:15.692 [Test worker] INFO reactor.Flux.MapFuseable.2 - | onSubscribe([Fuseable] FluxMapFuseable.MapFuseableSubscriber)
        23:48:15.692 [Test worker] INFO reactor.Flux.MapFuseable.2 - | request(32)
        23:48:16.695 [Test worker] INFO reactor.Flux.MapFuseable.2 - | onNext(ADAM)
        23:48:17.700 [Test worker] INFO reactor.Flux.MapFuseable.2 - | onNext(JILL)
        23:48:18.711 [Test worker] INFO reactor.Flux.MapFuseable.2 - | onNext(JACK)
        23:48:18.712 [Test worker] INFO reactor.Flux.MapFuseable.2 - | onComplete()
```
now the same code but with the **publishOn()**
```java
public Flux<String> explore_publishOn(){

        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();
       return namesFlux.mergeWith(namesFlux1);
    }
```
and the same test case produce the following output.
```java
23:51:39.096 [Test worker] INFO reactor.Flux.MapFuseable.1 - | onSubscribe([Fuseable] FluxMapFuseable.MapFuseableSubscriber)
23:51:39.098 [Test worker] INFO reactor.Flux.MapFuseable.1 - | request(32)
23:51:39.100 [Test worker] INFO reactor.Flux.MapFuseable.2 - | onSubscribe([Fuseable] FluxMapFuseable.MapFuseableSubscriber)
23:51:39.100 [Test worker] INFO reactor.Flux.MapFuseable.2 - | request(32)
23:51:40.116 [parallel-1] INFO reactor.Flux.MapFuseable.1 - | onNext(ALEX)
23:51:40.116 [parallel-2] INFO reactor.Flux.MapFuseable.2 - | onNext(ADAM)
23:51:41.127 [parallel-1] INFO reactor.Flux.MapFuseable.1 - | onNext(BEN)
23:51:41.127 [parallel-2] INFO reactor.Flux.MapFuseable.2 - | onNext(JILL)
23:51:42.134 [parallel-1] INFO reactor.Flux.MapFuseable.1 - | onNext(CHLOE)
23:51:42.134 [parallel-2] INFO reactor.Flux.MapFuseable.2 - | onNext(JACK)
23:51:42.134 [parallel-1] INFO reactor.Flux.MapFuseable.1 - | onComplete()
23:51:42.134 [parallel-2] INFO reactor.Flux.MapFuseable.2 - | onComplete()
```
but for blocking calls the most preferable Schedulers is **boundedElastic()**
```java
public Flux<String> explore_publishOn(){

        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase)
                .log();

        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.boundedElastic())
                .map(this::upperCase)
                .map(s -> {
                    log.info("Name is : {}", s);
                    return s;
                })
                .log();
       return namesFlux.mergeWith(namesFlux1);
    }
```

### 15.2: Switching Threads using subscribeOn()
- This operator is used to hop the Thread of execution of the reactive pipeline from one to another
- subscribeOn() is used to influence the thread upstream()
  - It influences the operators above the subscribeOn() to switch the thread
  - It impacts the whole reactive pipeline
- When to use it ?
  - Blocking code is part of the library were publishOn() is not added to it

Suppose the code
```java
public Flux<String> explore_subscribeOn(){

        var namesFlux = flux1(namesList)
                .subscribeOn(Schedulers.boundedElastic())
                .log();

        var namesFlux1 = flux1(namesList1)
                .subscribeOn(Schedulers.boundedElastic())
                .map(s -> {
                    log.info("Name is : {}", s);
                    return s;
                })
                .log();
       return namesFlux.mergeWith(namesFlux1);
    }

    // this could be the function part of a library and we have no
    // control modifying this code
    private Flux<String> flux1(List<String> namesList) {
        return Flux.fromIterable(FluxAndMonoSchedulersService.namesList)
                .map(this::upperCase);
    }

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }
```
and the test case
```java
@Test
    void explore_subscribeOn() {

        var flux = fluxAndMonoSchedulersService.explore_subscribeOn();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }
```
with the following output
```java
00:13:39.200 [Test worker] INFO reactor.Flux.SubscribeOn.1 - onSubscribe(FluxSubscribeOn.SubscribeOnSubscriber)
00:13:39.203 [Test worker] INFO reactor.Flux.SubscribeOn.1 - request(32)
00:13:39.207 [Test worker] INFO reactor.Flux.Map.2 - onSubscribe(FluxMap.MapSubscriber)
00:13:39.208 [Test worker] INFO reactor.Flux.Map.2 - request(32)
00:13:40.224 [boundedElastic-2] INFO com.learnreactiveprogramming.service.FluxAndMonoSchedulersService - Name is : ALEX
00:13:40.225 [boundedElastic-1] INFO reactor.Flux.SubscribeOn.1 - onNext(ALEX)
00:13:40.225 [boundedElastic-2] INFO reactor.Flux.Map.2 - onNext(ALEX)
00:13:41.229 [boundedElastic-2] INFO com.learnreactiveprogramming.service.FluxAndMonoSchedulersService - Name is : BEN
00:13:41.229 [boundedElastic-2] INFO reactor.Flux.Map.2 - onNext(BEN)
00:13:41.229 [boundedElastic-1] INFO reactor.Flux.SubscribeOn.1 - onNext(BEN)
00:13:42.237 [boundedElastic-1] INFO reactor.Flux.SubscribeOn.1 - onNext(CHLOE)
00:13:42.237 [boundedElastic-2] INFO com.learnreactiveprogramming.service.FluxAndMonoSchedulersService - Name is : CHLOE
00:13:42.237 [boundedElastic-2] INFO reactor.Flux.Map.2 - onNext(CHLOE)
00:13:42.237 [boundedElastic-1] INFO reactor.Flux.SubscribeOn.1 - onComplete()
00:13:42.237 [boundedElastic-2] INFO reactor.Flux.Map.2 - onComplete()
```

### 15.3: Making Blocking calls from a Service
Suppose the code
```java
public Mono<Movie> getMovieByIdWithRevenue(long movieId) {
        var movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieId);
        var reviewsMono = reviewService.retrieveReviewsFlux(movieId).collectList();

        // getRevenue() is the remote blocking service
        var revenueMono = Mono.fromCallable(() -> revenueService.getRevenue(movieId))
                .subscribeOn(Schedulers.boundedElastic());

        return movieInfoMono
                .zipWith(reviewsMono, Movie::new)
                .zipWith(revenueMono, (movie, revenue) -> {
                    movie.setRevenue(revenue);
                    return movie;
                })
                .log();
    }
```
and the test case
```java
@Test
    void getMovieByIdWithRevenue() {
        long movieId = 100L;

        var movieMono = movieReactiveService.getMovieByIdWithRevenue(movieId);

        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertEquals("Batman Begins", movie.getMovie().getName());
                    assertEquals(2, movie.getReviewList().size());
                    assertNotNull(movie.getRevenue());
                })
                .verifyComplete();
    }
```
with the following output
```java
00:27:25.649 [Test worker] INFO reactor.Mono.Zip.1 - onSubscribe([Fuseable] MonoZip.ZipCoordinator)
00:27:25.652 [Test worker] INFO reactor.Mono.Zip.1 - request(unbounded)
00:27:26.668 [boundedElastic-1] INFO reactor.Mono.Zip.1 - onNext(Movie(movie=MovieInfo(movieInfoId=100, name=Batman Begins, year=2005, cast=[Christian Bale, Michael Cane], release_date=2005-06-15), reviewList=[Review(reviewId=1, movieInfoId=100, comment=Awesome Movie, rating=8.9), Review(reviewId=2, movieInfoId=100, comment=Excellent Movie, rating=9.0)], revenue=Revenue(movieInfoId=100, budget=1000000.0, boxOffice=5000000.0)))
00:27:26.690 [boundedElastic-1] INFO reactor.Mono.Zip.1 - onComplete()
BUILD SUCCESSFUL in 4s
```

## Section 18 : Explore Data Parallelism in Project Reactor

### 18.1: Parallelism using parallel() and runOn() operator

*ParallelFlux*
- The idea behind **ParallelFLux** is to leverage the multi-core processors that we have in today's hardware
- MultiCore = Process multiple things at the same time

Suppose the code
```java
    static List<String> namesList = List.of("alex", "ben", "chloe");

    public ParallelFlux<String> explore_parallel() {
        var noOfCores = Runtime.getRuntime().availableProcessors();
        log.info("noOfCores : {}", noOfCores);
        return Flux.fromIterable(namesList)
        //.publishOn(Schedulers.parallel())
        .parallel()
        .runOn(Schedulers.parallel())
        .map(this::upperCase)
        .log();
        }

private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
        }
```
and the test case
```java
    @Test
    void explore_parallel() {

        var flux = fluxAndMonoSchedulersService.explore_parallel();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();

    }
```
with the following output
````java
20:40:06.447 [Test worker] INFO reactor.Parallel.Map.1 - onSubscribe(FluxMap.MapSubscriber)
20:40:06.449 [Test worker] INFO reactor.Parallel.Map.1 - request(256)
20:40:06.452 [Test worker] INFO reactor.Parallel.Map.1 - onSubscribe(FluxMap.MapSubscriber)
20:40:06.452 [Test worker] INFO reactor.Parallel.Map.1 - request(256)
20:40:06.452 [Test worker] INFO reactor.Parallel.Map.1 - onSubscribe(FluxMap.MapSubscriber)
20:40:06.452 [Test worker] INFO reactor.Parallel.Map.1 - request(256)
20:40:06.453 [Test worker] INFO reactor.Parallel.Map.1 - onSubscribe(FluxMap.MapSubscriber)
20:40:06.453 [Test worker] INFO reactor.Parallel.Map.1 - request(256)
20:40:06.454 [Test worker] INFO reactor.Parallel.Map.1 - onSubscribe(FluxMap.MapSubscriber)
20:40:06.454 [Test worker] INFO reactor.Parallel.Map.1 - request(256)
20:40:06.454 [Test worker] INFO reactor.Parallel.Map.1 - onSubscribe(FluxMap.MapSubscriber)
20:40:06.454 [Test worker] INFO reactor.Parallel.Map.1 - request(256)
20:40:06.455 [Test worker] INFO reactor.Parallel.Map.1 - onSubscribe(FluxMap.MapSubscriber)
20:40:06.455 [Test worker] INFO reactor.Parallel.Map.1 - request(256)
20:40:06.455 [Test worker] INFO reactor.Parallel.Map.1 - onSubscribe(FluxMap.MapSubscriber)
20:40:06.456 [Test worker] INFO reactor.Parallel.Map.1 - request(256)
20:40:06.457 [parallel-8] INFO reactor.Parallel.Map.1 - onComplete()
20:40:06.457 [parallel-6] INFO reactor.Parallel.Map.1 - onComplete()
20:40:06.457 [parallel-5] INFO reactor.Parallel.Map.1 - onComplete()
20:40:06.457 [parallel-4] INFO reactor.Parallel.Map.1 - onComplete()
20:40:06.457 [parallel-7] INFO reactor.Parallel.Map.1 - onComplete()
20:40:07.459 [parallel-3] INFO reactor.Parallel.Map.1 - onNext(CHLOE)
20:40:07.459 [parallel-2] INFO reactor.Parallel.Map.1 - onNext(BEN)
20:40:07.459 [parallel-1] INFO reactor.Parallel.Map.1 - onNext(ALEX)
20:40:07.460 [parallel-2] INFO reactor.Parallel.Map.1 - onComplete()
20:40:07.460 [parallel-3] INFO reactor.Parallel.Map.1 - onComplete()
20:40:07.460 [parallel-1] INFO reactor.Parallel.Map.1 - onComplete()
````

### 18.2: Parallelism using flatMap()
suppose the code
```java
    static List<String> namesList = List.of("alex", "ben", "chloe");

    public Flux<String> explore_parallel_usingFlatmap() {

        return Flux.fromIterable(namesList)
                .flatMap(name -> Mono.just(name)
                        .map(this::upperCase)
                        .subscribeOn(Schedulers.parallel()))
                .log();
    }
```
and the test case
```java
    @Test
    void explore_parallel_usingFlatmap() {

        var flux = fluxAndMonoSchedulersService.explore_parallel_usingFlatmap();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }
```
with the following output
```java
21:05:15.037 [Test worker] INFO reactor.Flux.FlatMap.1 - onSubscribe(FluxFlatMap.FlatMapMain)
21:05:15.042 [Test worker] INFO reactor.Flux.FlatMap.1 - request(unbounded)
21:05:16.096 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(ALEX)
21:05:16.098 [parallel-3] INFO reactor.Flux.FlatMap.1 - onNext(CHLOE)
21:05:16.107 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(BEN)
21:05:16.107 [parallel-2] INFO reactor.Flux.FlatMap.1 - onComplete()
```

### 18.3: Parallelism using flatMapSequential()
- This operator helps to achieve concurrency and maintain the ordering of elements at the same time.

Suppose the code
```java
    static List<String> namesList = List.of("alex", "ben", "chloe");
    
    public Flux<String> explore_parallel_usingFlatmapsequential() {

        return Flux.fromIterable(namesList)
        .flatMapSequential(name -> Mono.just(name)
        .map(this::upperCase)
        .subscribeOn(Schedulers.parallel()))
        .log();
        }
```
and the test case
```java
    @Test
    void explore_parallel_usingFlatmapsequential() {

        var flux = fluxAndMonoSchedulersService.explore_parallel_usingFlatmapsequential();

        StepVerifier.create(flux)
                .expectNext("ALEX", "BEN", "CHLOE")
                .verifyComplete();
    }
```
with the following output
```java
21:09:53.808 [Test worker] INFO reactor.Flux.MergeSequential.1 - onSubscribe(FluxMergeSequential.MergeSequentialMain)
21:09:53.811 [Test worker] INFO reactor.Flux.MergeSequential.1 - request(unbounded)
21:09:54.848 [parallel-2] INFO reactor.Flux.MergeSequential.1 - onNext(ALEX)
21:09:54.849 [parallel-2] INFO reactor.Flux.MergeSequential.1 - onNext(BEN)
21:09:54.849 [parallel-2] INFO reactor.Flux.MergeSequential.1 - onNext(CHLOE)
21:09:54.849 [parallel-2] INFO reactor.Flux.MergeSequential.1 - onComplete()
```

## Section 19 : Cold & Hot Streams

**Cold Streams**
- Cold Stream is a type of Stream which emits the elements from beginning to end for every new subscription
- Examples of Cold Streams
  - HTTP Call with similar request
  - DB call with similar request

**Hot Streams**
- Data is emitted continuously
- Any subscriber will only get the current state of the Reactive Stream
  - Type 1: Waits for the first subscription from the subscriber and emits the data continuously
  - Type 2: Emits the data continuously withou the need for subscription
- Examples:
  - Stock Tickers - Emits stock updates continuously as they change
  - Uber Driver Tracking - Emits the current position of the Driver Continuously

### 19.1 : Cold Streams

Suppose the test code
```java
    void coldPublisherTest() {

        var flux = Flux.range(1, 10);

        flux.subscribe(i -> System.out.println("Subscriber 1 : " + i));
        
        flux.subscribe(i -> System.out.println("Subscriber 2 : " + i));
    }
```
with the following output
````java
21:27:13.241 [Test worker] DEBUG reactor.util.Loggers$LoggerFactory - Using Slf4j logging framework
Subscriber 1 : 1
Subscriber 1 : 2
Subscriber 1 : 3
Subscriber 1 : 4
Subscriber 1 : 5
Subscriber 1 : 6
Subscriber 1 : 7
Subscriber 1 : 8
Subscriber 1 : 9
Subscriber 1 : 10
Subscriber 2 : 1
Subscriber 2 : 2
Subscriber 2 : 3
Subscriber 2 : 4
Subscriber 2 : 5
Subscriber 2 : 6
Subscriber 2 : 7
Subscriber 2 : 8
Subscriber 2 : 9
Subscriber 2 : 10
````

### 19.2 : Hot Streams - ConnectableFlux

Suppose the test code
```java
    @Test
    void holdPublisherTest() {

        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<Integer> connectableFlux = flux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe(i -> System.out.println("Subscriber 1 : " + i));
        delay(4000);

        connectableFlux.subscribe(i -> System.out.println("Subscriber 2 : " + i));
        delay(10000);

    }
```
with the following output
```java
21:34:18.717 [Test worker] DEBUG reactor.util.Loggers$LoggerFactory - Using Slf4j logging framework
Subscriber 1 : 1
Subscriber 1 : 2
Subscriber 1 : 3
Subscriber 1 : 4
Subscriber 2 : 4
Subscriber 1 : 5
Subscriber 2 : 5
Subscriber 1 : 6
Subscriber 2 : 6
Subscriber 1 : 7
Subscriber 2 : 7
Subscriber 1 : 8
Subscriber 2 : 8
Subscriber 1 : 9
Subscriber 2 : 9
Subscriber 1 : 10
Subscriber 2 : 10
```

### 19.3 : Hot Streams - ConnectableFlux using autoConnect and RefCount

**autoConnect**
- Give the possibility to delay the flux until the specified numbers of subscriber have subscribed

Suppose the test code
```java
@Test
    void holdPublisherTest_autoConnect() {

        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        var hotSource = flux.publish().autoConnect(2); // Min subsriber

        hotSource.subscribe(i -> System.out.println("Subscriber 1 : " + i));
        delay(2000);

        hotSource.subscribe(i -> System.out.println("Subscriber 2 : " + i));
        System.out.println("Two Subscriber are connected");
        delay(2000);

        hotSource.subscribe(i -> System.out.println("Subscriber 3 : " + i));
        delay(10000);
    }
```
with the following output
```java
21:43:10.317 [Test worker] DEBUG reactor.util.Loggers$LoggerFactory - Using Slf4j logging framework
Two Subscriber are connected
Subscriber 1 : 1
Subscriber 2 : 1
Subscriber 1 : 2
Subscriber 2 : 2
Subscriber 3 : 2
Subscriber 1 : 3
Subscriber 2 : 3
Subscriber 3 : 3
Subscriber 1 : 4
Subscriber 2 : 4
Subscriber 3 : 4
Subscriber 1 : 5
Subscriber 2 : 5
Subscriber 3 : 5
Subscriber 1 : 6
Subscriber 2 : 6
Subscriber 3 : 6
Subscriber 1 : 7
Subscriber 2 : 7
Subscriber 3 : 7
Subscriber 1 : 8
Subscriber 2 : 8
Subscriber 3 : 8
Subscriber 1 : 9
Subscriber 2 : 9
Subscriber 3 : 9
Subscriber 1 : 10
Subscriber 2 : 10
Subscriber 3 : 10
```

**refCount**
- Has the capability to continuous Keep track the defined numbers of subscribers
- At some point if the number of subscribers goes down then it stops to emit values

Suppose the test code
```java
    @Test
    void holdPublisherTest_refCount() {

        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .doOnCancel(() -> System.out.println("Received Cancel Signal"));

        var hotSource = flux.publish().refCount(2);

        var disposable = hotSource.subscribe(i -> System.out.println("Subscriber 1 : " + i));
        delay(2000);

        var disposable1 = hotSource.subscribe(i -> System.out.println("Subscriber 2 : " + i));
        System.out.println("Two Subscriber are connected");
        delay(2000);
        disposable.dispose();
        disposable1.dispose();
        hotSource.subscribe(i -> System.out.println("Subscriber 3 : " + i));
        delay(2000);
        hotSource.subscribe(i -> System.out.println("Subscriber 4 : " + i));
        delay(10000);
    }
```
with th efollowing output
```java
22:10:22.156 [Test worker] DEBUG reactor.util.Loggers$LoggerFactory - Using Slf4j logging framework
Two Subscriber are connected
Subscriber 1 : 1
Subscriber 2 : 1
Received Cancel Signal
Subscriber 3 : 1
Subscriber 4 : 1
Subscriber 3 : 2
Subscriber 4 : 2
Subscriber 3 : 3
Subscriber 4 : 3
Subscriber 3 : 4
Subscriber 4 : 4
Subscriber 3 : 5
Subscriber 4 : 5
Subscriber 3 : 6
Subscriber 4 : 6
Subscriber 3 : 7
Subscriber 4 : 7
Subscriber 3 : 8
Subscriber 4 : 8
Subscriber 3 : 9
Subscriber 4 : 9
BUILD SUCCESSFUL in 18s
```

## Section 20 : Testing using VirtualTimeScheduler

### 20.1 : StepVerifier using VirtualTimeScheduler
- It provides fast test case runtime

Suppose the function with a delay of 1s introduced by *splitString_withDelay*
```java
    public Flux<String> namesFlux_concatmap(int stringLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                //.map(s -> s.toUpperCase())
                .filter(s -> s.length() > stringLength)
                .concatMap(s -> splitString_withDelay(s))
                .log();
    }
    
private Flux<String> splitString_withDelay(String name) {
        var charArray = name.split("");
        //int delay = new Random().nextInt(1000);
        var delay = 1000;
        return Flux.fromArray(charArray)
        .delayElements(Duration.ofMillis(delay));
        }
```
and the test case
```java
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
```
we see that the test complete in 9s due to the 1s delay and teh numbers of element
```java
22:20:25.613 [Test worker] INFO reactor.Flux.ConcatMap.1 - onSubscribe(FluxConcatMap.ConcatMapImmediate)
22:20:25.619 [Test worker] INFO reactor.Flux.ConcatMap.1 - request(unbounded)
22:20:26.672 [parallel-1] INFO reactor.Flux.ConcatMap.1 - onNext(A)
22:20:27.677 [parallel-2] INFO reactor.Flux.ConcatMap.1 - onNext(L)
22:20:28.678 [parallel-3] INFO reactor.Flux.ConcatMap.1 - onNext(E)
22:20:29.680 [parallel-4] INFO reactor.Flux.ConcatMap.1 - onNext(X)
22:20:30.685 [parallel-5] INFO reactor.Flux.ConcatMap.1 - onNext(C)
22:20:31.688 [parallel-6] INFO reactor.Flux.ConcatMap.1 - onNext(H)
22:20:32.699 [parallel-7] INFO reactor.Flux.ConcatMap.1 - onNext(L)
22:20:33.709 [parallel-8] INFO reactor.Flux.ConcatMap.1 - onNext(O)
22:20:34.713 [parallel-1] INFO reactor.Flux.ConcatMap.1 - onNext(E)
22:20:34.713 [parallel-1] INFO reactor.Flux.ConcatMap.1 - onComplete()
```
now we introduce the VirtualTimeScheduler in the test
```java
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
```
with the following output
```java
22:22:54.187 [Test worker] INFO reactor.Flux.ConcatMap.1 - onSubscribe(FluxConcatMap.ConcatMapImmediate)
22:22:54.191 [Test worker] INFO reactor.Flux.ConcatMap.1 - request(unbounded)
22:22:54.224 [Test worker] INFO reactor.Flux.ConcatMap.1 - onNext(A)
22:22:54.224 [Test worker] INFO reactor.Flux.ConcatMap.1 - onNext(L)
22:22:54.224 [Test worker] INFO reactor.Flux.ConcatMap.1 - onNext(E)
22:22:54.226 [Test worker] INFO reactor.Flux.ConcatMap.1 - onNext(X)
22:22:54.226 [Test worker] INFO reactor.Flux.ConcatMap.1 - onNext(C)
22:22:54.226 [Test worker] INFO reactor.Flux.ConcatMap.1 - onNext(H)
22:22:54.226 [Test worker] INFO reactor.Flux.ConcatMap.1 - onNext(L)
22:22:54.226 [Test worker] INFO reactor.Flux.ConcatMap.1 - onNext(O)
22:22:54.226 [Test worker] INFO reactor.Flux.ConcatMap.1 - onNext(E)
22:22:54.227 [Test worker] INFO reactor.Flux.ConcatMap.1 - onComplete()
```

## Section 22: Programmatically Creating a Flux/Mono

### 22.2: Create Flux using generate()
- this operator takes a initial value an a generator function as an input and continuously emit value
- This is also called Synchronous generate
- We will be able to generate the OnNext, OnComplete and onError events using the SynchronousSink class.
- Use this operator, if you have a use case to emit values from a starting value until a certain condition is met

Suppose the code
```java
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
```
and the test case
```java
    @Test
    void explore_generate() {

        var flux = fluxAndMonoGeneratorService.explore_generate().log();

        StepVerifier.create(flux)
                .expectNextCount(10)
                .verifyComplete();
    }
```
with the following output
```java
00:34:50.566 [Test worker] INFO reactor.Flux.Generate.1 - | onSubscribe([Fuseable] FluxGenerate.GenerateSubscription)
00:34:50.573 [Test worker] INFO reactor.Flux.Generate.1 - | request(unbounded)
00:34:50.574 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(2)
00:34:50.574 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(4)
00:34:50.574 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(6)
00:34:50.574 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(8)
00:34:50.574 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(10)
00:34:50.574 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(12)
00:34:50.574 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(14)
00:34:50.575 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(16)
00:34:50.575 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(18)
00:34:50.575 [Test worker] INFO reactor.Flux.Generate.1 - | onNext(20)
00:34:50.576 [Test worker] INFO reactor.Flux.Generate.1 - | onComplete()
```

### 22.3: Create Flux using create()
- Used to bridge an existing API in to the Reactive World
- This is Asynchronous and Multithreaded
  - We can generate/emit these event from multiple threads
- We will be able to generate the OnNext, OnComplete and OnError events using the **FluxSink** class
- Multiple Emissions in a single round is supported

Suppose the code
```java
    public static List<String> names() {
        delay(1000);
        return List.of("alex", "ben", "chloe");
    }
    
    public Flux<String> exlpore_create() {
        return Flux.create(fluxSink -> {
            names().forEach(fluxSink::next);
            fluxSink.complete();
        });
    }
```
and the test case
```java
    @Test
    void exlpore_create() {

        var flux = fluxAndMonoGeneratorService.exlpore_create().log();

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }
```
with the following output
```java
00:42:57.152 [Test worker] INFO reactor.Flux.Create.1 - onSubscribe(FluxCreate.BufferAsyncSink)
00:42:57.158 [Test worker] INFO reactor.Flux.Create.1 - request(unbounded)
00:42:58.167 [Test worker] INFO reactor.Flux.Create.1 - onNext(alex)
00:42:58.167 [Test worker] INFO reactor.Flux.Create.1 - onNext(ben)
00:42:58.167 [Test worker] INFO reactor.Flux.Create.1 - onNext(chloe)
00:42:58.168 [Test worker] INFO reactor.Flux.Create.1 - onComplete()
BUILD SUCCESSFUL in 3s
```

now suppose the async code with *CompletableFuture*
```java
    public Flux<String> exlpore_create() {
        return Flux.create(fluxSink -> {
            //names().forEach(fluxSink::next);
            CompletableFuture.supplyAsync(() -> names())
                    .thenAccept(names -> {
                        names.forEach(fluxSink::next);
                    })
                    .thenRun(fluxSink::complete);
        });
    }
```
and the same test case with the following output
```java
00:51:07.722 [Test worker] INFO reactor.Flux.Create.1 - onSubscribe(FluxCreate.BufferAsyncSink)
00:51:07.726 [Test worker] INFO reactor.Flux.Create.1 - request(unbounded)
00:51:08.737 [ForkJoinPool.commonPool-worker-3] INFO reactor.Flux.Create.1 - onNext(alex)
00:51:08.737 [ForkJoinPool.commonPool-worker-3] INFO reactor.Flux.Create.1 - onNext(ben)
00:51:08.737 [ForkJoinPool.commonPool-worker-3] INFO reactor.Flux.Create.1 - onNext(chloe)
00:51:08.738 [ForkJoinPool.commonPool-worker-3] INFO reactor.Flux.Create.1 - onComplete()
```

finally supposed the multithreaded code
```java
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
    
    public void sendEvents(FluxSink<String> fluxSink) {
        CompletableFuture.supplyAsync(() -> names())
                .thenAccept(names -> {
                    names.forEach(fluxSink::next);
                })
                .thenRun(fluxSink::complete);
    }
```
and the test case
```java
    @Test
    void exlpore_create() {

        var flux = fluxAndMonoGeneratorService.exlpore_create().log();

        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }
```
with the following output (observe different thread pool)
```java
00:56:12.515 [Test worker] INFO reactor.Flux.Create.1 - onSubscribe(FluxCreate.BufferAsyncSink)
00:56:12.520 [Test worker] INFO reactor.Flux.Create.1 - request(unbounded)
00:56:13.553 [ForkJoinPool.commonPool-worker-3] INFO reactor.Flux.Create.1 - onNext(alex)
00:56:13.554 [ForkJoinPool.commonPool-worker-3] INFO reactor.Flux.Create.1 - onNext(ben)
00:56:13.554 [ForkJoinPool.commonPool-worker-3] INFO reactor.Flux.Create.1 - onNext(chloe)
00:56:14.650 [ForkJoinPool.commonPool-worker-5] INFO reactor.Flux.Create.1 - onNext(alex)
00:56:14.650 [ForkJoinPool.commonPool-worker-5] INFO reactor.Flux.Create.1 - onNext(ben)
00:56:14.651 [ForkJoinPool.commonPool-worker-5] INFO reactor.Flux.Create.1 - onNext(chloe)
00:56:14.652 [ForkJoinPool.commonPool-worker-5] INFO reactor.Flux.Create.1 - onComplete()
```

### 22.4: Create a Mono using create()
- We will be able to generate on the OnNext, OnComplete and onError events using **MonoSink** class

Suppose the code
```java
    public Mono<String> exlpore_create_mono() {
        return Mono.create(monoSink -> {
            monoSink.success("alex");
        });
    }
```
and the test case
```java
    @Test
    void exlpore_create_mono() {

        var mono = fluxAndMonoGeneratorService.exlpore_create_mono().log();

        StepVerifier.create(mono)
                .expectNext("alex")
                .verifyComplete();

    }
```
with the following output
```java
01:03:24.712 [Test worker] INFO reactor.Mono.Create.1 - onSubscribe(MonoCreate.DefaultMonoSink)
01:03:24.724 [Test worker] INFO reactor.Mono.Create.1 - request(unbounded)
01:03:24.726 [Test worker] INFO reactor.Mono.Create.1 - onNext(alex)
01:03:24.727 [Test worker] INFO reactor.Mono.Create.1 - onComplete()
```

### 22.5: Create a Flux using handle()
- It is an instance operator from which we can apply transformation like map and filter function together

Suppose the code
```java
    public Flux<String> explore_handle() {

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .handle((name, sink) -> {
                    if(name.length()>3){
                        sink.next(name.toUpperCase());
                    }
                });
    }
```
and the test case
```java
    @Test
    void explore_handle() {

        var flux = fluxAndMonoGeneratorService.explore_handle().log();

        StepVerifier.create(flux)
                .expectNextCount(2)
                .verifyComplete();
    }
```
with the following output
```java
01:14:19.165 [Test worker] INFO reactor.Flux.HandleFuseable.1 - | onSubscribe([Fuseable] FluxHandleFuseable.HandleFuseableSubscriber)
01:14:19.170 [Test worker] INFO reactor.Flux.HandleFuseable.1 - | request(unbounded)
01:14:19.171 [Test worker] INFO reactor.Flux.HandleFuseable.1 - | onNext(ALEX)
01:14:19.171 [Test worker] INFO reactor.Flux.HandleFuseable.1 - | onNext(CHLOE)
01:14:19.172 [Test worker] INFO reactor.Flux.HandleFuseable.1 - | onComplete()
```

## Section 23: Debugging in Project Reactor

### 23.1: Debug Exceptions using Hooks.onOperatorDebug()
- Put it to the main class
- Is not recommended for **prod** as it may slow down the performance of the app

### 23.2: Debug Exceptions using checkpoint operators

### 23.3: Production-ready Global Debugging using ReactorDebugAgent
- This is recommended option for debugging exception in project reactor
- Java agent that runs alongside your app
- It collects the stack trace information of each operator without any performance overhead
- Separate dependencies **reactor-tools**
  - ReactorDebugAgent.init()
  - ReactorDebugAgent.processExistingClasses() => only in Test


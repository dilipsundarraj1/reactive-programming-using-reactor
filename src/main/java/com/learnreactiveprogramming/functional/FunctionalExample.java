package com.learnreactiveprogramming.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionalExample {

    public static List<String> namesGreaterThanSize(List<String> namesList, int size) {
        return namesList.stream()
                //.parallel()
                .filter(s -> s.length() > size)
                //.sorted()
                //.distinct()
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {

        var namesList = List.of("alex",/* "alex",*/ "ben", "chloe", "adam");
        var newNamesList = namesGreaterThanSize(namesList, 3);
        System.out.println("newNamesList  :"+ newNamesList);

    }
}
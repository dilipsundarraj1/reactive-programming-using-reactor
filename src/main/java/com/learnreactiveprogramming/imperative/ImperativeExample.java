package com.learnreactiveprogramming.imperative;

import java.util.ArrayList;
import java.util.List;

public class ImperativeExample {

    public static List<String> namesGreaterThanSize(List<String> namesList, int size){
        var newNamesList = new ArrayList<String>();
        for(String name : namesList){
            if(name.length()>size && !newNamesList.contains(name))
                //newNamesList.add(name.toUpperCase());
            newNamesList.add(name);
        }

        return newNamesList;
    }

    public static void main(String[] args) {

        var namesList = List.of("alex", "ben", "chloe", "adam","adam");
        var newNamesList =namesGreaterThanSize(namesList, 3);
        System.out.println("newNamesList  :"+ newNamesList);

    }
}
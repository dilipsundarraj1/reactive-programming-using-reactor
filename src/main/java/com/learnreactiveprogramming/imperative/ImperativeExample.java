package com.learnreactiveprogramming.imperative;

import java.util.ArrayList;
import java.util.List;

public class ImperativeExample {

    public static void main(String[] args) {
        var namesList = List.of("alex", "ben", "chloe", "adam", "adam");
        var newNameList = namesGreaterThanSize(namesList,3);
        System.out.println("newNamesList: " + newNameList);
    }

    private static List<String> namesGreaterThanSize(List<String> namesList, int size) {
        var newNamesList = new ArrayList<String>();

       for(String name: namesList){
           if(name.length()>size && !newNamesList.contains(name)){
               newNamesList.add(name.toUpperCase());
           }
       }

        return newNamesList;
    }
}

package com.example.yelprecommendation.Classes;

import java.util.HashMap;
import java.util.Map;

class DisjointUnionSets {
    Map<String, Integer> rank;
    Map<String, String> parent;

    // Constructor
    public DisjointUnionSets(String[] elements) {
        rank = new HashMap<>();
        parent = new HashMap<>();
        makeSet(elements);
    }

    // Creates n sets with single item in each
    void makeSet(String[] elements) {
        for (String element : elements) {
            parent.put(element, element);
            rank.put(element, 0);
        }
    }

    String find(String x) {
        if (!parent.get(x).equals(x)) {
            parent.put(x, find(parent.get(x)));
        }
        return parent.get(x);
    }

    void union(String x, String y) {
        String xRoot = find(x);
        String yRoot = find(y);

        if (xRoot.equals(yRoot))
            return;

        if (rank.get(xRoot) < rank.get(yRoot))
            parent.put(xRoot, yRoot);
        else if (rank.get(yRoot) < rank.get(xRoot))
            parent.put(yRoot, xRoot);
        else {
            parent.put(yRoot, xRoot);
            rank.put(xRoot, rank.get(xRoot) + 1);
        }
    }

    // Driver code
    public static void main(String[] args) {
        // Let there be 5 persons with ids as "one", "two", "three", "four", "five"
        String[] elements = {"one", "two", "three", "four", "five"};
        DisjointUnionSets dus = new DisjointUnionSets(elements);

        dus.union("one", "two");

        // "one" is a friend of "three"
        dus.union("two", "three");

        // "four" is a friend of "three"
        dus.union("three", "four");

//        dus.union("one", "four");


        System.out.println(dus.parent);
        System.out.println(dus);

        System.out.println(dus.find("one").equals(dus.find("four")));
        System.out.println(dus.rank);

        // Check if "four" is a friend of "one"
        if (dus.find("four").equals(dus.find("one")))
            System.out.println("Yes");
        else
            System.out.println("No");

        // Check if "two" is a friend of "one"
        if (dus.find("one").equals(dus.find("five")))
            System.out.println("Yes");
        else
            System.out.println("No");
    }

}




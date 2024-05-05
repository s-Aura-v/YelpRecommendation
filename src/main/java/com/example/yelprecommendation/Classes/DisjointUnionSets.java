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
}




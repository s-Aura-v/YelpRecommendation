package com.example.yelprecommendation.Classes;
/* Java Program to Implement Dijkstra's Algorithm : Adapted from geeks to geeks
* https://www.geeksforgeeks.org/dijkstras-shortest-path-algorithm-in-java-using-priorityqueue/
* Priority Queue
 */

import java.util.*;
public class Graph {

    // Member variables of this class
    int[] dist;
    private Set<Integer> settled;
    private PriorityQueue<Node> pq;
    // Number of vertices
    private int V;
    List<List<Node> > adj;

    // Constructor of this class
    public Graph(int V)
    {
        // This keyword refers to current object itself
        this.V = V;
        dist = new int[V];
        settled = new HashSet<Integer>();
        pq = new PriorityQueue<Node>(V, new Node());
    }
    // Dijkstra's Algorithm
    public void dijkstra(List<List<Node> > adj, int src)
    {
        this.adj = adj;

        for (int i = 0; i < V; i++)
            dist[i] = Integer.MAX_VALUE;

        pq.add(new Node(src, 0));
        dist[src] = 0;

        while (settled.size() != V) {

            if (pq.isEmpty())
                return;

            int u = pq.remove().node;

            if (settled.contains(u))
                continue;
            settled.add(u);

            e_Neighbours(u);
        }
    }
    private void e_Neighbours(int u)
    {

        int edgeDistance = -1;
        int newDistance = -1;

        // All the neighbors of v
        for (int i = 0; i < adj.get(u).size(); i++) {
            Node v = adj.get(u).get(i);

            // If current node hasn't already been processed
            if (!settled.contains(v.node)) {
                edgeDistance = v.cost;
                newDistance = dist[u] + edgeDistance;

                // If new distance is cheaper in cost
                if (newDistance < dist[v.node])
                    dist[v.node] = newDistance;
                pq.add(new Node(v.node, dist[v.node]));
            }
        }
    }
}

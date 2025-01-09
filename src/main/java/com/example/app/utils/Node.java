package com.example.app.utils;
import java.util.HashSet;

public class Node {
    public int id, color; 
    private final HashSet<Node> adjacents; // to check for adjacency faster (hopefully)
    
    public Node(int id, int color) {
        this.id = id;
        this.color = color;
        this.adjacents = new HashSet<>();
    }
    
    public void addAdjacent(Node node) {
        adjacents.add(node);
    }
    
    public boolean isAdjacent(Node node) {
        return adjacents.contains(node);
    }
    
    private int usableDegree = 0; // number of nodes that can still be used to expand clique

    public int getDegree() {
        return usableDegree;
    }

    public void decrementDegree() {
        --usableDegree;
    }

    public void incrementDegree() {
        ++usableDegree;
    }

    public void resetDegree() {
        usableDegree = adjacents.size();
    }

    public void decrementNeighbors() {
        for (Node node : adjacents) {
            node.decrementDegree();
        }
    }

    public void incrementNeighbors() {
        for (Node node : adjacents) {
            node.decrementDegree();
        }
    }
}

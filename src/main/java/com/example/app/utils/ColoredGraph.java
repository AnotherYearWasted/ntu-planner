package com.example.app.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class ColoredGraph {
    private final ArrayList<Node> nodes;
    private int colors;

    public ColoredGraph() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    private int[] taken; // backtrack array, basically a list of all ids taken up to the current layer

    private boolean backtrackCliques(int layer, ArrayList<Node> remaining) {
        // hook condition: all colors taken, return the found clique
        if (layer == colors) return true;

        // basically, iterate through all nodes with the current color, and check if they can be added to the clique. just brute force

        // a feeble attempt at optimization :) trying to skip whatever nodes that have the same color as the layer
        int nextColor = 0;
        while (nextColor < remaining.size() && remaining.get(nextColor).color == layer) ++nextColor;
        
        for (int i = 0; i < nextColor; i++) {
            Node current = remaining.get(i);
            ArrayList<Node> nextRemaining = new ArrayList<>();
            
            // trying to reserve memory so that the allocations dont slow down runtime
            nextRemaining.ensureCapacity(remaining.size() - nextColor);
            
            int last = layer;
            for (int j = nextColor; j < remaining.size(); j++) {
                if (remaining.get(j).isAdjacent(current)) {
                    if (remaining.get(j).color - last > 1) break;
                    nextRemaining.add(remaining.get(j));
                    last = remaining.get(j).color;
                }
            }
            if (last == colors - 1) {
                taken[layer] = current.id;
                for (int j = 0; j < remaining.size(); j++) {
                    if (!remaining.get(j).isAdjacent(current)) {
                        remaining.get(j).decrementNeighbors();
                    }
                }
                nextRemaining.sort(Comparator.comparingInt((Node node) -> node.color).thenComparingInt(node -> -node.getDegree()));
                if (backtrackCliques(layer + 1, nextRemaining)) return true;
                for (int j = 0; j < remaining.size(); j++) {
                    if (!remaining.get(j).isAdjacent(current)) 
                        remaining.get(j).incrementNeighbors();
                }
            }
        }
        return false;
    }

    private void normalizeGraph() {
        // resetting degrees
        for (Node node : nodes) node.resetDegree();

        // reassigning colors to start from 0
        HashMap<Integer, Integer> colorMap = new HashMap<>();
        colors = 0;
        for (Node node : nodes) {
            if (!colorMap.containsKey(node.color)) {
                colorMap.put(node.color, colors++);
            }
            node.color = colorMap.get(node.color);
        }

        // sorting nodes by degree, and then by color
        nodes.sort(Comparator.comparingInt((Node node) -> node.color).thenComparingInt(node -> -node.getDegree()));

        // setting up taken
        taken = new int[colors];
    }

    public ArrayList<int[]> findCliques() {
        normalizeGraph();

        boolean found = backtrackCliques(0, new ArrayList<>(nodes));

        ArrayList<int[]> results = new ArrayList<>();
        if (found) results.add(taken.clone());
        
        return results;
    }
}
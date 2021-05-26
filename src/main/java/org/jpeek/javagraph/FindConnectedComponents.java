package org.jpeek.javagraph;

import java.util.ArrayList;

public class FindConnectedComponents {

  int V;
  ArrayList<ArrayList<Integer>> graph;

  public FindConnectedComponents(int V) {
    this.V = V;
    graph = new ArrayList<>();

    for (int i = 0; i < V; i++) {
      graph.add(i, new ArrayList<>());
    }
  }

  void DFS(int v, boolean[] visited) {
    visited[v] = true;
    System.out.print(v + " ");
    for (int x : graph.get(v)) {
      if (!visited[x]) {
        DFS(x, visited);
      }
    }
  }

  public int connectedComponents() {
    int countConnectedComponents = 0;
    boolean[] visited = new boolean[V];
    for (int v = 0; v < V; ++v) {
      if (!visited[v]) {
        DFS(v, visited);
        System.out.println();
        countConnectedComponents++;
      }
    }
    return countConnectedComponents;
  }


  public void addEdge(int src, int dest) {
    graph.get(src).add(dest);
    graph.get(dest).add(src);
  }
}

// Written by Elvin Latifi

import java.util.*;

public class ListGraph<T> {
    private final Map<T, HashSet<Edge<T>>> nodes = new HashMap<>();

    public void add(T node) {
        nodes.putIfAbsent(node, new HashSet<>());
    }

    public void remove(T node) {
        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException();
        }
        nodes.remove(node);
        for (Set<Edge<T>> edgeSet : nodes.values()) {
            edgeSet.removeIf(edge -> edge.getDestination() == node);
        }
    }

    public void connect(T n1, T n2, String name, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException();
        }
        else if (getEdgeBetween(n1, n2) != null) {
            throw new IllegalStateException();
        }
        Edge<T> e1 = new Edge<>(n1, name, weight);
        nodes.get(n2).add(e1);
        Edge<T> e2 = new Edge<>(n2, name, weight);
        nodes.get(n1).add(e2);
    }

    public void disconnect(T n1, T n2) {
        if (!nodes.containsKey(n1) || !nodes.containsKey(n2)) {
            throw new NoSuchElementException();
        }
        Edge<T> e1 = getEdgeBetween(n1, n2);
        if (e1 == null) {
            throw new IllegalStateException();
        }
        Edge<T> e2 = getEdgeBetween(n2, n1);
        nodes.get(n1).remove(e1);
        nodes.get(n2).remove(e2);
    }

    public void setConnectionWeight(T n1, T n2, int newWeight) {
        if (getEdgeBetween(n1, n2) == null) {
            throw new IllegalStateException();
        }
        getEdgeBetween(n1, n2).setWeight(newWeight);
        getEdgeBetween(n2, n1).setWeight(newWeight);
    }

    public Set<T> getNodes() {
        return Collections.unmodifiableSet(nodes.keySet());
    }

    public Collection<Edge<T>> getEdgesFrom(T node) {
        if (!nodes.containsKey(node)) {
            throw new NoSuchElementException();
        }
        return Collections.unmodifiableSet(nodes.get(node));
    }

    public Edge<T> getEdgeBetween(T n1, T n2) {
        if (!nodes.containsKey(n1) || !nodes.containsKey(n2)) {
            throw new NoSuchElementException();
        }
        for (Edge<T> edge : nodes.get(n1)) {
            if (edge.getDestination().equals(n2)) {
                return edge;
            }
        }
        return null;
    }

    public boolean pathExists(T n1, T n2) {
        if (!nodes.containsKey(n1) || !nodes.containsKey(n2)) {
            return false;
        }
        HashSet<T> seen = new HashSet<>();
        depthFirstSearch(n1, seen);
        return seen.contains(n2);
    }

    private void depthFirstSearch(T node, Set<T> seen) {
        seen.add(node);
        for (Edge<T> edge : nodes.get(node)) {
            if (!seen.contains(edge.getDestination())) {
                depthFirstSearch(edge.getDestination(), seen);
            }
        }
    }

    private List<Edge<T>> gatherPath(T n1, T n2, Map<T, T> via) {
        ArrayList<Edge<T>> path = new ArrayList<>();
        T currentNode = n2;
        while (!currentNode.equals(n1)) {
            T whereFrom = via.get(currentNode);
            Edge<T> edge = getEdgeBetween(whereFrom, currentNode);
            path.add(edge);
            currentNode = whereFrom;
        }
        Collections.reverse(path);
        return path;
    }

    private void depthFirstSearch2(T n1, T n2, Set<T> seen, Map<T, T> via) {
        seen.add(n1);
        via.put(n1, n2);
        for (Edge<T> edge : nodes.get(n1)) {
            if (!seen.contains(edge.getDestination())) {
                depthFirstSearch2(edge.getDestination(), n1, seen, via);
            }
        }
    }

    public List<Edge<T>> getFastestPath(T from, T to) {
        if (!pathExists(from, to))
            return null;
        ArrayList<NodeTable<T>> nodeTables = new ArrayList<>();
        for (T node : nodes.keySet()) {
            nodeTables.add(new NodeTable<>(node));
        }
        NodeTable<T> start = null;
        NodeTable<T> end = null;
        for (NodeTable<T> nt : nodeTables) {
            if (nt.getNode().equals(from))
                start = nt;
            if (nt.getNode().equals(to))
                end = nt;
        }
        start.setTime(0);
        start.determine();
        NodeTable<T> where = start;
        while (!end.isDetermined()) {
            for (Edge<T> e : nodes.get(where.getNode())) {
                NodeTable<T> current = null;
                for (NodeTable<T> nt : nodeTables) {
                    if (e.getDestination().equals(nt.getNode()))
                        current = nt;
                }
                if (e.getWeight() + where.getTime() < current.getTime()) {
                    current.setTime(e.getWeight() + where.getTime());
                    current.setVia(where.getNode());
                }
            }
            where = getNextNodeTable(nodeTables);
        }
        HashMap<T, T> via = createMap(nodeTables, start, end);
        return gatherPath(from, to, via);
    }

    private NodeTable<T> getNextNodeTable(ArrayList<NodeTable<T>> nodeTables) {
        int currentMin = Integer.MAX_VALUE;
        NodeTable<T> currentMinNT = null;
        for (NodeTable<T> nt : nodeTables) {
            if (!nt.isDetermined() && nt.getTime() < currentMin) {
                currentMin = nt.getTime();
                currentMinNT = nt;
            }
        }
        currentMinNT.determine();
        return currentMinNT;
    }

    private HashMap<T, T> createMap(ArrayList<NodeTable<T>> nodeTables, NodeTable<T> start, NodeTable<T> end) {
        HashMap<T, T> via = new HashMap<>();
        NodeTable<T> current = end;
        while (!current.equals(start)) {
            via.put(current.getNode(), current.getVia());
            for (NodeTable<T> nt : nodeTables) {
                if (current.getVia().equals(nt.getNode())) {
                    current = nt;
                    break;
                }
            }
        }
        via.put(start.getNode(), null);
        return via;
    }

    @Override
    public String toString() {
        String info = "";
        for (T node : nodes.keySet()) {
            info += "Node: " + node.toString() + "\nEdges:";
            int counter = 1;
            for (Edge<T> edge : nodes.get(node)) {
                info += "\nEdge " + counter + ":" + edge.toString();
                counter++;
            }
        }
        return info;
    }
}

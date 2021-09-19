// Written by Elvin Latifi

public class Edge<T> {
    private final T node;
    private final String name;
    private int weight;

    public Edge(T node, String name, int weight) {
        this.node = node;
        this.name = name;
        this.weight = weight;
    }

    public T getDestination() {
        return node;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int newWeight) {
        if (newWeight < 0) {
            throw new IllegalArgumentException();
        }
        weight = newWeight;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("to %s by %s takes %d", node.toString(), name, weight);
    }
}

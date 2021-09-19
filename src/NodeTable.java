// Written by Elvin Latifi

public class NodeTable<T> {
    private final T node;
    private T via;
    private int time = Integer.MAX_VALUE;
    private boolean determined;

    public NodeTable(T node) {
        this.node = node;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void determine() {
        determined = true;
    }

    public boolean isDetermined() {
        return determined;
    }

    public T getNode() {
        return node;
    }

    public void setVia(T via) {
        this.via = via;
    }

    public T getVia() {
        return via;
    }
}

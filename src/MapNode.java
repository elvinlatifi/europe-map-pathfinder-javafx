// Written by Elvin Latifi

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MapNode extends Circle {
    private final String name;
    private final double x;
    private final double y;

    public MapNode(String name, double x, double y) {
        super(x, y, 10, Color.BLUE);
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public void select() {
        this.setFill(Color.RED);
    }

    public void deselect() {
        this.setFill(Color.BLUE);
    }

    public String getName() {
        return name;
    }

    public double getXValue() {
        return x;
    }

    public double getYValue() {
        return y;
    }

    public String toString(){
        return name;
    }
}

// Written by Elvin Latifi

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ConnectionDialog extends Alert {
    private final TextField nameField = new TextField();
    private final TextField timeField = new TextField();

    public ConnectionDialog() {
        super(AlertType.CONFIRMATION);
        this.setTitle("Connection");
        GridPane grid = new GridPane();
        grid.addRow(0, new Label("Name: "), nameField);
        grid.addRow(1, new Label("Time: "), timeField);
        getDialogPane().setContent(grid);
    }

    public String getName() {
        return nameField.getText();
    }

    public void setName(String name) {
        nameField.setText(name);
        nameField.setEditable(false);
    }

    public int getTime() {
        return Integer.parseInt(timeField.getText());
    }

    public void setTime(String time) {
        timeField.setText(time);
        timeField.setEditable(false);
    }
}

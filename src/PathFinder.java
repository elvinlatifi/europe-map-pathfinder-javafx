// Written by Elvin Latifi

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class PathFinder extends Application {
    // Main instance variables:
    private Stage stage;
    private ListGraph<MapNode> cityGraph = new ListGraph<>();
    private final ImageView imageView = new ImageView();
    private final Pane outputArea = new Pane();
    private final BorderPane root = new BorderPane();
    private boolean saved = true;
    private MapNode from;
    private MapNode to;

    // Buttons:
    private Button findPath;
    private Button showCon;
    private Button newPlace;
    private Button newCon;
    private Button changeCon;

    // Menu Items:
    private MenuBar mb;
    private Menu fileMenu;
    private MenuItem save;
    private MenuItem saveImage;
    private MenuItem newMap;
    private MenuItem open;
    private MenuItem exit;



    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PathFinder");
        stage = primaryStage;

        // Set up main buttons
        FlowPane fp = new FlowPane();
        fp.setHgap(10);
        fp.setPrefSize(540, 50);
        fp.setAlignment(Pos.CENTER);
        findPath = new Button("Find Path");
        findPath.setOnAction(new FindPathHandler());
        showCon = new Button("Show Connection");
        showCon.setOnAction(new ShowConnectionHandler());
        newPlace = new Button("New Place");
        newPlace.setOnAction(new NewPlaceHandler());
        newCon = new Button("New Connection");
        newCon.setOnAction(new NewConnectionHandler());
        changeCon = new Button("Change Connection");
        changeCon.setOnAction(new ChangeConnectionHandler());
        fp.getChildren().addAll(findPath, showCon, newPlace, newCon, changeCon);
        root.setCenter(fp);

        // Set up File menu
        mb = new MenuBar();
        root.setTop(mb);
        fileMenu = new Menu("File");
        mb.getMenus().add(fileMenu);
        newMap = new MenuItem("New Map");
        newMap.setOnAction(new NewMapHandler());
        open = new MenuItem("Open");
        open.setOnAction(new OpenHandler());
        save = new MenuItem("Save");
        save.setOnAction(new SaveHandler());
        saveImage = new MenuItem("Save Image");
        saveImage.setOnAction(new SaveImageHandler());
        exit = new MenuItem("Exit");
        exit.setOnAction(new ExitMenuHandler());
        fileMenu.getItems().addAll(newMap, open, save, saveImage, exit);

        // Set up outputArea
        root.setBottom(outputArea);

        // Set ID's
        setIDs();

        // Disable relevant buttons
        disableButtons();

        // Set and present scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new ExitWindowHandler());
        primaryStage.show();
    }

    // General methods
    private void reset() {
        cityGraph = new ListGraph<>();
        outputArea.getChildren().clear();
        outputArea.getChildren().add(imageView);
        from = null;
        to = null;
    }

    private void disableButtons() {
        saveImage.setDisable(true);
        save.setDisable(true);
        findPath.setDisable(true);
        showCon.setDisable(true);
        newPlace.setDisable(true);
        newCon.setDisable(true);
        changeCon.setDisable(true);
    }

    private void enableButtons() {
        saveImage.setDisable(false);
        save.setDisable(false);
        findPath.setDisable(false);
        showCon.setDisable(false);
        newPlace.setDisable(false);
        newCon.setDisable(false);
        changeCon.setDisable(false);
    }

    private void setIDs() {
        mb.setId("menu");
        fileMenu.setId("menuFile");
        newMap.setId("menuNewMap");
        open.setId("menuOpenFile");
        save.setId("menuSaveFile");
        saveImage.setId("menuSaveImage");
        exit.setId("menuExit");
        findPath.setId("btnFindPath");
        showCon.setId("btnShowConnection");
        newPlace.setId("btnNewPlace");
        changeCon.setId("btnChangeConnection");
        newCon.setId("btnNewConnection");
        outputArea.setId("outputArea");
    }

    private Alert defaultErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle("Error!");
        return alert;
    }

    private boolean continueWithoutSaving() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Warning!");
        alert.setHeaderText(null);
        alert.setContentText("Unsaved changes, continue anyway?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.CANCEL)
            return false;
        return true;
    }

    // Handlers
    class NewMapHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (!saved) {
                if (!continueWithoutSaving())
                    return;
            }
            reset();
            Image image = new Image("file:europa.gif");
            imageView.setImage(image);
            stage.sizeToScene();
            enableButtons();
            saved = false;
        }
    }

    class NewPlaceHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            newPlace.setDisable(true);
            outputArea.setOnMouseClicked(new ClickHandler());
            outputArea.setCursor(Cursor.CROSSHAIR);
        }
    }

    class ClickHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Label nameOfCity;
            TextInputDialog tid = new TextInputDialog();
            tid.setTitle("Name");
            tid.setHeaderText(null);
            tid.setContentText("Name of place:");
            Optional<String> name = tid.showAndWait();
            if (name.isPresent()) {
                if (name.get().isEmpty() || name.get().isBlank()) {
                    Alert emptyError = defaultErrorAlert();
                    emptyError.setContentText("Name can not be empty!");
                    emptyError.showAndWait();
                    newPlace.setDisable(false);
                    outputArea.setOnMouseClicked(null);
                    outputArea.setCursor(Cursor.DEFAULT);
                    return;
                }
                else
                    nameOfCity = new Label(name.get());
            }
            else {
                newPlace.setDisable(false);
                outputArea.setOnMouseClicked(null);
                outputArea.setCursor(Cursor.DEFAULT);
                return;
            }
            double x = event.getX();
            double y = event.getY();
            nameOfCity.relocate(x + 5, y + 5); // Adjust Label position to below Node
            nameOfCity.setStyle("-fx-font-weight: bold");
            nameOfCity.setMouseTransparent(true);
            MapNode mapNode = new MapNode(name.get(), x, y);
            mapNode.setOnMouseClicked(new NodeClickedHandler());
            outputArea.getChildren().addAll(mapNode, nameOfCity);
            cityGraph.add(mapNode);
            mapNode.setId(mapNode.getName());
            outputArea.setOnMouseClicked(null);
            outputArea.setCursor(Cursor.DEFAULT);
            newPlace.setDisable(false);
            saved = false;
        }
    }

    class NodeClickedHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            MapNode clicked = (MapNode)event.getSource();
            if (from == null) {
                from = clicked;
                clicked.select();
            }
            else if (to == null && clicked != from) {
                to = clicked;
                clicked.select();
            }
            else if (clicked == from) {
                from = to;
                to = null;
                clicked.deselect();
            }
            else if (clicked == to) {
                to = null;
                clicked.deselect();
            }
        }
    }

    class NewConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (from == null || to == null) {
                Alert alert = defaultErrorAlert();
                alert.setContentText("Two places must be selected!");
                alert.showAndWait();
                return;
            }
            if (cityGraph.getEdgeBetween(from, to) != null) {
                Alert alert = defaultErrorAlert();
                alert.setContentText("Connection already exists!");
                alert.showAndWait();
                return;
            }
            ConnectionDialog cd = new ConnectionDialog();
            cd.setHeaderText("Connection from " + from.getName() + " to " + to.getName());
            Optional<ButtonType> result = cd.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL)
                return;

            try {
                if (cd.getName().isEmpty()) {
                    Alert emptyAlert = defaultErrorAlert();
                    emptyAlert.setContentText("Name can not be empty!");
                    emptyAlert.showAndWait();
                    return;
                }
                int time = cd.getTime();
                cityGraph.connect(from, to, cd.getName(), time);
                createConnection(from, to);
                saved = false;
            }
            catch (NumberFormatException e) {
                Alert numberError = defaultErrorAlert();
                numberError.setContentText("Time must be a number!");
                numberError.showAndWait();
            }
        }
    }
    // Help method
    private void createConnection(MapNode from, MapNode to) {
        Line line = new Line(from.getXValue(), from.getYValue(), to.getXValue(), to.getYValue());
        line.setMouseTransparent(true);
        outputArea.getChildren().add(line);
    }

    class ShowConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (from == null || to == null) {
                Alert alert = defaultErrorAlert();
                alert.setContentText("Two places must be selected!");
                alert.showAndWait();
                return;
            }
            Edge<MapNode> connection = cityGraph.getEdgeBetween(from, to);
            if (connection == null) {
                Alert alert = defaultErrorAlert();
                alert.setContentText("No connection exists between selected nodes!");
                alert.showAndWait();
                return;
            }
            ConnectionDialog connectionInfo = new ConnectionDialog();
            connectionInfo.setHeaderText("Connection from " + from.getName() + " to " + to.getName());
            connectionInfo.setName(connection.getName());
            connectionInfo.setTime("" + connection.getWeight());
            connectionInfo.showAndWait();
        }
    }

    class ChangeConnectionHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (from == null || to == null) {
                Alert alert = defaultErrorAlert();
                alert.setContentText("Two places must be selected!");
                alert.showAndWait();
                return;
            }
            Edge<MapNode> connection = cityGraph.getEdgeBetween(from, to);
            if (connection == null) {
                Alert alert = defaultErrorAlert();
                alert.setContentText("No connection exists between selected nodes!");
                alert.showAndWait();
                return;
            }
            ConnectionDialog cd = new ConnectionDialog();
            cd.setHeaderText("Connection from " + from.getName() + " to " + to.getName());
            cd.setName(from.getName());
            Optional<ButtonType> result = cd.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL)
                return;
            try {
                int time = cd.getTime();
                cityGraph.setConnectionWeight(from, to, time);
                saved = false;
            }
            catch (NumberFormatException e) {
                Alert numberError = defaultErrorAlert();
                numberError.setContentText("Time must be a number!");
                numberError.showAndWait();
            }
        }
    }

    class FindPathHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (from == null || to == null) {
                Alert alert = defaultErrorAlert();
                alert.setContentText("Two places must be selected!");
                alert.showAndWait();
                return;
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message");
            alert.setHeaderText("The Path from " + from.getName() + " to " + to.getName() + ":");
            String pathInfo = "";
            int totalTime = 0;
            List<Edge<MapNode>> edges = cityGraph.getFastestPath(from, to);
            if (edges == null) {
                alert.setHeaderText("No path exists between " + from.getName() + " and " + to.getName());
                alert.setContentText(null);
                alert.showAndWait();
                return;
            }
            for (Edge<MapNode> edge : edges) {
                pathInfo += edge + "\n";
                totalTime += edge.getWeight();
            }
            pathInfo = "Total " + totalTime + "\n" + pathInfo;
            TextArea textArea = new TextArea(pathInfo);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        }
    }

    class OpenHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            if (!saved) {
                if (!continueWithoutSaving())
                    return;
            }
            HashMap<String, MapNode> stringToCity = new HashMap<>();
            reset();

            try {
                FileReader fr = new FileReader("europa.graph");
                BufferedReader in = new BufferedReader(fr);

                // Set up image
                Image image = new Image(in.readLine());
                imageView.setImage(image);
                stage.sizeToScene();

                // Load in cities
                String[] tokens = in.readLine().split(";");
                for (int i = 0; i < tokens.length; i+=3) {
                    MapNode city = new MapNode(tokens[i], Double.parseDouble(tokens[i+1]), Double.parseDouble(tokens[i+2]));
                    cityGraph.add(city);
                    stringToCity.put(tokens[i], city);
                }

                // Load in connections
                String line = "";
                while ((line = in.readLine()) != null) {
                    String[] tokens2 = line.split(";");
                    MapNode c1 = stringToCity.get(tokens2[0]);
                    MapNode c2 = stringToCity.get(tokens2[1]);
                    if (cityGraph.getEdgeBetween(c1, c2) == null) {
                        cityGraph.connect(c1, c2, tokens2[2], Integer.parseInt(tokens2[3]));
                    }
                }
                fr.close();
                in.close();
            }
            catch (FileNotFoundException e) {
                Alert errorAlert = defaultErrorAlert();
                errorAlert.setContentText("File not found!");
                errorAlert.showAndWait();
                return;
            }
            catch (Exception e) {

            }
            loadNodes();
            loadEdges();
            enableButtons();
            saved = true;
        }
    }
    // Help methods for OpenHandler
    private void loadNodes() {
        for (MapNode city : cityGraph.getNodes()) {
            double x = city.getXValue();
            double y = city.getYValue();
            Label nameOfCity = new Label(city.getName());
            nameOfCity.setMouseTransparent(true);
            nameOfCity.relocate(x + 5, y + 5); // Adjust Label position to below Node
            nameOfCity.setStyle("-fx-font-weight: bold");
            city.setOnMouseClicked(new NodeClickedHandler());
            outputArea.getChildren().addAll(city, nameOfCity);
            city.setId(city.getName());
        }
    }
    private void loadEdges() {
        for (MapNode city : cityGraph.getNodes()) {
            for (Edge<MapNode> edge : cityGraph.getEdgesFrom(city)) {
                MapNode to = edge.getDestination();
                createConnection(city, to);
            }
        }
    }

    class SaveHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle (ActionEvent event) {
            try {
                FileWriter fw = new FileWriter("europa.graph");
                PrintWriter out = new PrintWriter(fw);
                out.println(imageView.getImage().getUrl());
                int nrOfNodes = cityGraph.getNodes().size();
                int i = 1;
                for (MapNode city : cityGraph.getNodes()) {
                    if (i < nrOfNodes)
                        out.print(city.getName() + ";" + city.getXValue() + ";" + city.getYValue() + ";");
                    else
                        out.println(city.getName() + ";" + city.getXValue() + ";" + city.getYValue());
                    i++;
                }
                for (MapNode city : cityGraph.getNodes()) {
                    for (Edge<MapNode> edge : cityGraph.getEdgesFrom(city)) {
                        out.println(city.getName() + ";" + edge.getDestination() + ";" + edge.getName() + ";" + edge.getWeight());
                    }
                }
                fw.close();
                out.close();
                saved = true;
            }
            catch (IOException e) {

            }
        }
    }

    class SaveImageHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            try {
                WritableImage image = outputArea.snapshot(null, null);
                BufferedImage bi = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bi, "png", new File("capture.png"));
            }
            catch (IOException e) {

            }
        }
    }

    class ExitMenuHandler implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    class ExitWindowHandler implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent event) {
            if (!saved) {
                if (!continueWithoutSaving())
                    event.consume();
            }
        }
    }
}

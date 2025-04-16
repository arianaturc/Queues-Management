package graphicalUserInterface;

import businessLogic.SimulationManager;
import businessLogic.SelectionPolicy;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;

public class SimulationFrame {

    private TextField tfNumberOfClients;
    private TextField tfNumberOfServers;
    private TextField tfTimeLimit;
    private TextField tfMinArrival;
    private TextField tfMaxArrival;
    private TextField tfMinProcessing;
    private TextField tfMaxProcessing;
    private ComboBox<SelectionPolicy> cbSelectionPolicy;
    private TextArea simulationOutputArea;

    public void launchUI() {
        Platform.runLater(() -> {
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Queues Management Application");

            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #f5f5f5;");

            Label title = new Label("Queues Management System");
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            title.setAlignment(Pos.CENTER);

            VBox topSection = new VBox(20);
            topSection.setAlignment(Pos.TOP_CENTER);

            GridPane inputGrid = new GridPane();
            inputGrid.setHgap(10);
            inputGrid.setVgap(10);
            inputGrid.setAlignment(Pos.CENTER);

            tfTimeLimit = addLabeledTextField(inputGrid, "Simulation time", 0, 5);
            tfNumberOfServers = addLabeledTextField(inputGrid, "Number of Queues", 1, 0);
            tfNumberOfClients = addLabeledTextField(inputGrid, "Number of Clients", 0, 0);
            cbSelectionPolicy = new ComboBox<>();
            cbSelectionPolicy.getItems().addAll(SelectionPolicy.SHORTEST_QUEUE, SelectionPolicy.SHORTEST_TIME);
            cbSelectionPolicy.getSelectionModel().select(SelectionPolicy.SHORTEST_TIME);
            inputGrid.add(new Label("Strategy Type"), 5 * 2, 1);
            inputGrid.add(cbSelectionPolicy, 5 * 2 +1, 1);

            tfMinArrival = addLabeledTextField(inputGrid, "Minimum Arrival Time", 0, 1);
            tfMaxArrival = addLabeledTextField(inputGrid, "Maximum Arrival Time", 1, 1);
            tfMinProcessing = addLabeledTextField(inputGrid, "Minimum Service Time", 0, 2);
            tfMaxProcessing = addLabeledTextField(inputGrid, "Maximum Service Time", 1, 2);

            topSection.getChildren().addAll(title, inputGrid);
            root.setTop(topSection);

            simulationOutputArea = new TextArea();
            simulationOutputArea.setEditable(false);
            simulationOutputArea.setWrapText(true);
            simulationOutputArea.setPrefHeight(300);
            simulationOutputArea.setPrefWidth(500);

            HBox centerBox = new HBox(simulationOutputArea);
            centerBox.setAlignment(Pos.CENTER);
            centerBox.setPadding(new Insets(20));

            root.setCenter(centerBox);
            Button startButton = new Button("Start Simulation");
            startButton.setStyle("-fx-background-color: orange; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10px 30px;");
            startButton.setOnAction(e -> startSimulation());

            HBox bottomBox = new HBox(startButton);
            bottomBox.setAlignment(Pos.CENTER);
            bottomBox.setPadding(new Insets(20));
            root.setBottom(bottomBox);

            Scene scene = new Scene(root, 1000, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    private TextField addLabeledTextField(GridPane grid, String labelText, int row, int col) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        grid.add(label, col * 2, row);
        grid.add(textField, col * 2 + 1, row);
        return textField;
    }

    private void startSimulation() {
        try {
            int numberOfClients = Integer.parseInt(tfNumberOfClients.getText());
            int numberOfServers = Integer.parseInt(tfNumberOfServers.getText());
            int timeLimit = Integer.parseInt(tfTimeLimit.getText());
            int minArrival = Integer.parseInt(tfMinArrival.getText());
            int maxArrival = Integer.parseInt(tfMaxArrival.getText());
            int minProcessing = Integer.parseInt(tfMinProcessing.getText());
            int maxProcessing = Integer.parseInt(tfMaxProcessing.getText());
            SelectionPolicy policy = cbSelectionPolicy.getValue();

            String outputPath = "C:\\Users\\arian\\Documents\\an2_utcn\\sem2\\fpt\\PT2025_30422_Turc_Ariana_Assignment_2\\src\\main\\java\\test_gui.txt";

            simulationOutputArea.clear();

            SimulationManager manager = new SimulationManager(
                    numberOfClients, numberOfServers, timeLimit,
                    minArrival, maxArrival, minProcessing, maxProcessing,
                    policy, outputPath
            );

            Thread simulationThread = new Thread(manager);
            simulationThread.start();

            startFileReadingThread(outputPath);

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers in all fields.");
        }
    }

    private void startFileReadingThread(String outputPath) {
        Thread fileReaderThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(outputPath))) {
                String line;
                while (true) {
                    while ((line = reader.readLine()) != null) {
                        appendToOutput(line);
                    }
                    Thread.sleep(500);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        fileReaderThread.setDaemon(true);
        fileReaderThread.start();
    }

    public void appendToOutput(String message) {
        Platform.runLater(() -> simulationOutputArea.appendText(message + "\n"));
    }

    private void showError(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, msg);
            alert.showAndWait();
        });
    }
}
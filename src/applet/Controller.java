package applet;

import flow.FlowController;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private StackPane gameContainer;

    private Parent game;
    private FlowController flowController;
    @FXML
    private Spinner<Integer> dimensionSpinner;
    @FXML
    private Button submitButton;
    @FXML
    private Button resetButton;
    @FXML
    private ProgressIndicator progressionIndicator;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/flow.fxml"));
        flowController = new FlowController();
        loader.setController(flowController);
        try {
            game = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


        gameContainer.getChildren().add(game);
        dimensionSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 9, 4));
        flowController.dimensionProperty().bind(dimensionSpinner.valueProperty());

        submitButton.setOnAction(event -> {
            Task task = flowController.generateSolveTask();
            progressionIndicator.visibleProperty().bind(task.runningProperty());
            submitButton.visibleProperty().bind(task.runningProperty().not());
            Thread th = new Thread(task);
            th.start();
        });
        resetButton.setOnAction(event -> flowController.rebuild(dimensionSpinner.getValue()));
    }
}

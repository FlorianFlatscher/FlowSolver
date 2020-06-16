import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.InvalidPropertiesFormatException;
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
        dimensionSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 7, 4));
        flowController.dimensionProperty().bind(dimensionSpinner.valueProperty());

        submitButton.setOnAction(actionEvent -> {
            try {
                System.out.println("flowController.getState().solve() = " + Arrays.toString(flowController.getState().solve()));
            } catch (InvalidPropertiesFormatException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        });
    }
}

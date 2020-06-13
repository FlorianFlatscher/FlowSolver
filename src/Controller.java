import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
    }
}

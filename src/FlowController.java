import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.ResourceBundle;

public class FlowController implements Initializable {
    @FXML
    private Group pathGroup;
    @FXML
    private GridPane gameGrid;

    private SimpleIntegerProperty dimension = new SimpleIntegerProperty();

    private Circle[][] shapes;

    private GameState state;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dimension.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldN, Number newNumber) {
                //Reset Grid and State
                int newN = ((Integer) newNumber);
                state = new GameState(newN);

                Node node = gameGrid.getChildren().get(0);
                gameGrid.getChildren().clear();
                gameGrid.getChildren().add(0,node);


                gameGrid.getRowConstraints().clear();
                gameGrid.getColumnConstraints().clear();
                for (int i = 0; i < newN; i++) {
                    ColumnConstraints colConst = new ColumnConstraints();
                    colConst.setPercentWidth(100.0 / newN);
                    gameGrid.getColumnConstraints().add(colConst);

                    RowConstraints rowConst = new RowConstraints();
                    rowConst.setPercentHeight(100.0 / newN);
                    gameGrid.getRowConstraints().add(rowConst);
                }

                shapes = new Circle[newN][newN];

                for (int x = 0; x < newN; x++) {
                    for (int y = 0; y < newN; y++) {
                        shapes[x][y] = new Circle(30);
                        shapes[x][y].setVisible(false);
                        StackPane stackPane = new StackPane(shapes[x][y]);

                        int finalX = x;
                        int finalY = y;
                        stackPane.setOnMouseClicked((event) -> {
                            int code = 0;
                            switch (event.getButton()) {
                                case PRIMARY:
                                    code = state.chicleUpId(finalX, finalY);
                                    break;

                                case SECONDARY:
                                    code = state.chicleUpDown(finalX, finalY);
                                    break;
                            }
                            shapes[finalX][finalY].setFill(codeToColor(code));

                            if (code > 0) {
                                shapes[finalX][finalY].setVisible(true);
                            } else {
                                shapes[finalX][finalY].setVisible(false);
                            }
                        });
                        gameGrid.add(stackPane, x, y);
                    }
                }
            }
        });
        dimension.set(4);
    }

    public int getDimension() {
        return dimension.get();
    }

    public SimpleIntegerProperty dimensionProperty() {
        return dimension;
    }

    private Color codeToColor (int code) {
        switch (code) {
            case 0:
                return Color.BLACK;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.YELLOW;
            case 3:
                return Color.RED;
            case 4:
                return Color.BLUE;
            case 5:
                return Color.MAGENTA;
            case 6:
                return Color.AQUA;
        }
        return null;
    }

    public GameState getState() {
        return state;
    }
}

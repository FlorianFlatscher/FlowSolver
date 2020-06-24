package flow;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.ResourceBundle;

public class FlowController implements Initializable {
    public static final Paint[] pointColors = new Paint[]{Color.BLACK, Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE, Color.MAGENTA, Color.AQUA, Color.BROWN, Color.ORANGE};

    @FXML
    public Group pathGroup;
    @FXML
    public GridPane gameGrid;

    private SimpleIntegerProperty dimension = new SimpleIntegerProperty();

    private Circle[][] shapes;

    private GameState state;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dimension.addListener((observableValue, oldN, newNumber) -> {
            rebuild((Integer) newNumber);
        });
    }

    public void rebuild(Integer dimension) {
        //Reset Grid and State
        int newN = dimension;
        state = new GameState(newN);

        pathGroup.getChildren().clear();

        gameGrid.getChildren().clear();
        gameGrid.setStyle("-fx-border-color: black;");

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

                stackPane.setStyle("-fx-border-color: black;");

                int finalX = x;
                int finalY = y;
                stackPane.setOnMouseClicked((event) -> {
                    pathGroup.getChildren().clear();
                    int code = 0;
                    switch (event.getButton()) {
                        case PRIMARY:
                            code = state.cycleUpId(finalX, finalY);
                            break;

                        case SECONDARY:
                            code = state.cycleUpDown(finalX, finalY);
                            break;
                    }
                    shapes[finalX][finalY].setFill(pointColors[code]);

                    if (code > 0) {
                        shapes[finalX][finalY].setVisible(true);
                    } else {
                        code = state.resetPos(finalX, finalY);
                        shapes[finalX][finalY].setVisible(false);
                    }
                });
                gameGrid.add(stackPane, x, y);
            }
        }
    }

    public int getDimension() {
        return dimension.get();
    }

    public SimpleIntegerProperty dimensionProperty() {
        return dimension;
    }

    public GameState getState() {
        return state;
    }

    public Task generateSolveTask() {
        Task<PathArrayList[]> task = new Task<PathArrayList[]>() {
            @Override
            protected PathArrayList[] call() {

                try {
                    return FlowController.this.getState().solve(FlowController.this);
                } catch (InvalidPropertiesFormatException e) {
                    this.updateMessage(e.getMessage());
                    this.cancel();
                }
                return null;
            }
        };
        task.setOnSucceeded(workerStateEvent -> {
            PathArrayList[] solve = task.getValue();
            System.out.println("finished");
            if (solve == null) {
                new Alert(Alert.AlertType.ERROR, "No solution found (134)").show();
                return;
            }
            for (int i = 0; i < solve.length; i++) {
                if (solve[i] == null)
                    continue;
                Path path = new Path();
                path.getElements().add(new MoveTo(solve[i].get(0).getX(), solve[i].get(0).getY()));
                for (int l = 1; l < solve[i].size(); l++) {
                    path.getElements().add(new LineTo(solve[i].get(l).getX(), solve[i].get(l).getY()));
                }

                path.setStroke(pointColors[i]);
                path.setStrokeWidth(5);

                final double scX = gameGrid.getWidth() / gameGrid.getColumnCount();
                final double scY = gameGrid.getHeight() / gameGrid.getRowCount();

                for (PathElement element : path.getElements()) {
                    if (element instanceof MoveTo) {
                        MoveTo moveTo = (MoveTo) element;
                        moveTo.setX(moveTo.getX() * scX + scX / 2);
                        moveTo.setY(moveTo.getY() * scY + scY / 2);
                    }

                    if (element instanceof LineTo) {
                        LineTo lineTo = (LineTo) element;
                        lineTo.setX(lineTo.getX() * scX + scX / 2);
                        lineTo.setY(lineTo.getY() * scY + scY / 2);
                    }
                }
                pathGroup.getChildren().add(path);
            }
        });
        task.setOnCancelled(workerStateEvent -> new Alert(Alert.AlertType.ERROR, workerStateEvent.getSource().getMessage()).show());
        return task;
    }
}

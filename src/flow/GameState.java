package flow;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;


public class GameState {
    private int[][] board;
    private int[][] pathBoard;
    private Location[][] points;
    private ArrayList<Integer> pointIds;
    private PathArrayList[] paths;

    public static final int[] fastSin = new int[]{0, 1, 0, -1};
    public static final int[] fastCos = new int[]{1, 0, -1, 0};

    public GameState(int dimension) {
        board = new int[dimension][dimension];

    }

    private GameState(GameState old, PathArrayList[] newPaths, int[][] newPathBoard) {
        points = deepCopyLocationMatrix(old.points);
        pointIds = new ArrayList<>(old.pointIds);

        paths = newPaths;
        pathBoard = newPathBoard;
    }

    public static int[][] deepCopyIntMatrix(int[][] input) {
        if (input == null)
            return null;
        int[][] result = new int[input.length][];
        for (int r = 0; r < input.length; r++) {
            result[r] = input[r].clone();
        }
        return result;
    }

    public static Location[][] deepCopyLocationMatrix(Location[][] input) {
        if (input == null)
            return null;
        Location[][] result = new Location[input.length][input[0].length];
        for (int r = 0; r < input.length; r++) {
            for (int i = 0; i < input[r].length; i++) {
                if (input[r][i] != null)
                    result[r][i] = input[r][i].clone();
            }
        }
        return result;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }


    public int cycleUpId(int x, int y) {
        board[x][y] += 1;

        if (board[x][y] >= FlowController.pointColors.length) {
            board[x][y] = 0;
        }
        return board[x][y];
    }

    public int cycleUpDown(int x, int y) {
        board[x][y] -= 1;
        if (board[x][y] < 0) {
            board[x][y] = FlowController.pointColors.length - 1;
        }
        return board[x][y];
    }

    public int resetPos(int x, int y) {
        return board[x][y] = 0;
    }

    public void compile() throws InvalidPropertiesFormatException {
        ArrayList<Integer> openIds = new ArrayList<>();
        pointIds = new ArrayList<>();
        pathBoard = deepCopyIntMatrix(board);
        points = new Location[FlowController.pointColors.length][2];
        paths = new PathArrayList[FlowController.pointColors.length];

        for (int x = 0; x < pathBoard.length; x++) {
            for (int y = 0; y < pathBoard[x].length; y++) {
                int value = pathBoard[x][y];
                if (value > 0) {
                    if (points[value][0] == null) {
                        points[value][0] = new Location(x, y);
                        openIds.add(value);
                    } else if (points[value][1] == null) {
                        points[value][1] = new Location(x, y);
                        openIds.remove((Integer) value);
                        pointIds.add(value);
                    } else {
                        throw new InvalidPropertiesFormatException("To much points with id " + value);
                    }
                }
            }
        }
        if (pointIds.size() == 0 || openIds.size() > 0) {
            throw new InvalidPropertiesFormatException("Invalid number of points");
        }
        for (Integer pointId : pointIds) {
            paths[pointId] = new PathArrayList();
            paths[pointId].add(new Location(points[pointId][0].getX(), points[pointId][0].getY()));
        }
    }

    private GameState move(int pathIndex, int deltaX, int deltaY) {
        PathArrayList path = paths[pathIndex];
        Location pathElement = path.get(path.size() - 1);
        int x, y;


        x = pathElement.getX();
        y = pathElement.getY();

        if (points[pathIndex][1].getX() == x && points[pathIndex][1].getY() == y) {
            return null;
        }

        if (x + deltaX >= 0 && x + deltaX < pathBoard.length && y + deltaY >= 0 && y + deltaY < pathBoard[0].length) {
            if (pathBoard[x + deltaX][y + deltaY] == 0 || points[pathIndex][1].getX() == x + deltaX && points[pathIndex][1].getY() == y + deltaY) {
                PathArrayList[] newPaths = new PathArrayList[FlowController.pointColors.length];
                for (int i = 0, pathsLength = paths.length; i < pathsLength; i++) {
                    PathArrayList oldPath = paths[i];
                    if (oldPath != null) {
                        newPaths[i] = new PathArrayList(oldPath);
                    }
                }


                newPaths[pathIndex].add(new Location(x + deltaX, y + deltaY));

                int[][] newBoard = deepCopyIntMatrix(pathBoard);
                newBoard[x + deltaX][y + deltaY] = pathIndex;

                return new GameState(this, newPaths, newBoard);
            }
        }
        return null;
    }

    private boolean solved() {
        int test = 0;
        for (int i = 1; i < paths.length; i++) {
            PathArrayList path = paths[i];
            if (path == null)
                continue;
            Location pathElement = path.get(path.size() - 1);
            int x, y;

            x = pathElement.getX();
            y = pathElement.getY();

            if (!(x == points[i][1].getX() && y == points[i][1].getY())) {
                return false;
            }

        }
        return true;
    }

    public PathArrayList[] solve(FlowController controller) throws InvalidPropertiesFormatException {
        compile();
        return innerSolve(controller);
    }

    private PathArrayList[] innerSolve(FlowController controller) {
        if (solved()) {
            return paths;
        }

        Platform.runLater(() -> {
            PathArrayList[] solve = paths;
            if (solve == null) {
                new Alert(Alert.AlertType.ERROR, "No solution found").show();
                return;
            }
            controller.pathGroup.getChildren().clear();
            for (int i = 0; i < solve.length; i++) {
                if (solve[i] == null)
                    continue;
                Path path = new Path();
                path.getElements().add(new MoveTo(solve[i].get(0).getX(), solve[i].get(0).getY()));
                for (int l = 1; l < solve[i].size(); l++) {
                    path.getElements().add(new LineTo(solve[i].get(l).getX(), solve[i].get(l).getY()));
                }

                path.setStroke(FlowController.pointColors[i]);
                path.setStrokeWidth(5);

                final double scX = controller.gameGrid.getWidth() / controller.gameGrid.getColumnCount();
                final double scY = controller.gameGrid.getHeight() / controller.gameGrid.getRowCount();

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
                controller.pathGroup.getChildren().add(path);
            }
        });

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int pathIndex = 0; pathIndex < paths.length; pathIndex++) {
            PathArrayList path = paths[pathIndex];
            if (path == null) {
                continue;
            }
            for (int i = 0; i < 4; i++) {
                GameState move = move(pathIndex, fastCos[i], fastSin[i]);
                if (move != null) {
                    PathArrayList[] movePath = move.innerSolve(controller);
                    if (movePath != null) {
                        return movePath;
                    }
                }
            }
        }
        return null;
    }
}


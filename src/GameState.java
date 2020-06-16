import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;

import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;


public class GameState {
    private int[][] board;
    private Location[][] points;
    private ArrayList<Integer> pointIds;
    private Path[] paths;

    public static final int[] fastSin = new int[]{0, 1, 0, -1};
    public static final int[] fastCos = new int[]{1, 0, -1, 0};

    public GameState(int dimension) {
        board = new int[dimension][dimension];
        points = new Location[6][2];
        pointIds = new ArrayList<>();
        paths = new Path[6];
    }

    private GameState(GameState old, Path[] newPaths, int[][] newBoard) {
        points = deepCopyLocationMatrix(old.points);
        pointIds = new ArrayList<>(old.pointIds);

        paths = newPaths;
        board = newBoard;
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
        Location[][] result = new Location[input.length][];
        for (int r = 0; r < input.length; r++) {
            result[r] = input[r].clone();
        }
        return result;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int chicleUpId(int x, int y) {
        board[x][y] += 1;

        if (board[x][y] > 6) {
            board[x][y] = 0;
        }
        return board[x][y];
    }

    public int chicleUpDown(int x, int y) {
        board[x][y] -= 1;
        if (board[x][y] < 0) {
            board[x][y] = 5;
        }
        return board[x][y];
    }

    public void compile() throws InvalidPropertiesFormatException {
        ArrayList<Integer> openIds = new ArrayList<>();
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
                int value = board[x][y];
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
        if (pointIds.size() == 0) {
            throw new InvalidPropertiesFormatException("Invalid number of points");
        }
        for (Integer pointId : pointIds) {
            paths[pointId] = (new Path(new MoveTo(points[pointId][0].getX(), points[pointId][0].getY())));
        }
    }

    private GameState move(int pathIndex, int deltaX, int deltaY) {
        Path path = paths[pathIndex];
        PathElement pathElement = path.getElements().get(path.getElements().size() - 1);
        int x, y;

        if (pathElement instanceof MoveTo) {
            MoveTo moveTo = (MoveTo) pathElement;
            x = ((int) (moveTo.getX() + 0.1));
            y = ((int) (moveTo.getY() + 0.1));
        } else if (pathElement instanceof LineTo) {
            LineTo pathTo = (LineTo) pathElement;
            x = ((int) (pathTo.getX() + 0.1));
            y = ((int) (pathTo.getY() + 0.1));
        } else {
            throw new NullPointerException();
        }

        if (x + deltaX > 0 && x + deltaX < board.length && y + deltaY > 0 && y + deltaY < board[0].length && (board[x + deltaX][y + deltaY] == 0)) {
            Path[] newPaths = new Path[6];
            for (int i = 0, pathsLength = paths.length; i < pathsLength; i++) {
                Path oldPath = paths[i];
                if (oldPath != null) {
                    newPaths[i] = new Path(new ArrayList<>(oldPath.getElements()));
                }
            }

            newPaths[pathIndex].getElements().add(new LineTo(x + deltaX, y + deltaY));

            int[][] newBoard = deepCopyIntMatrix(board);
            newBoard[x][y] = pathIndex;

            return new GameState(this, newPaths, newBoard);
        }

        return null;
    }

    private boolean solved() {
        for (int i = 0; i < paths.length; i++) {
            Path path = paths[i];
            if (path == null)
                continue;
            PathElement pathElement = path.getElements().get(path.getElements().size() - 1);
            int x, y;

            if (pathElement instanceof LineTo) {
                LineTo pathTo = (LineTo) pathElement;
                x = ((int) (pathTo.getX() + 0.1));
                y = ((int) (pathTo.getY() + 0.1));

                if (!(x == points[i][1].getX() && y == points[i][1].getY())) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    public Path[] solve() throws InvalidPropertiesFormatException {
        compile();
        return innerSolve();
    }

    private Path[] innerSolve() {
        if (solved()) {
            return paths;
        }

        for (int pathIndex = 0; pathIndex < paths.length; pathIndex++) {
            Path path = paths[pathIndex];
            if (path == null) {
                continue;
            }
            for (int i = 0; i < 4; i++) {
                GameState move = move(pathIndex, fastCos[i], fastSin[i]);
                if (move != null) {
                    Path[] movePath = move.innerSolve();
                    if (movePath != null) {
                        return movePath;
                    }
                }
            }
        }
        return null;
    }
}

import javafx.scene.shape.Circle;

public class GameState {
    private int[][] board;

    public GameState(int dimension) {
        board = new int[dimension][dimension];
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }
}

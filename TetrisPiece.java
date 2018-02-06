import java.awt.*;
import java.util.Random;

/**
 * Created by Abel on 9/26/17.
 */
public class TetrisPiece {
    private static int pieceIdCounter = 0;
    private int id;
    private static final Random rand = new Random();
    private static final int J = 0, L = 1, T = 2, Z = 3, S = 4, I = 5, O = 6;
    private static final Color[] colors = {Color.blue, Color.red, Color.orange, Color.magenta, Color.yellow, Color.cyan, Color.green};
    private Color color;
    private final static int[][][] pieces = {
            {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}, // J piece
            {{1, -1}, {0, -1}, {0, 0}, {0, 1}}, // L piece
            {{-1, 0}, {0, 0}, {1, 0}, {0, 1}}, // T piece
            {{0, -1}, {0, 0}, {1, 0}, {1, 1}}, // Z piece
            {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}, // S piece
            {{0, -1}, {0, 0}, {0, 1}, {0, 2}}, // I piece
            {{0, 0}, {1, 0}, {0, 1}, {1, 1}} // O Piece
    };
    private int[][] piece;
    private int pieceNum;

    public TetrisPiece() {
        id = pieceIdCounter;
        pieceIdCounter++;
        pieceNum = rand.nextInt(7);
        piece = new int[4][2];
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                piece[i][j] = pieces[pieceNum][i][j];
            }
        }
        color = colors[pieceNum];
    }


    public TetrisPiece(int pNum) {
        pieceNum = pNum;
        piece = new int[4][2];
        // System.out.println("Piece num = " + pieceNum);
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
//                System.out.println("i = " + i + " j = " + j);
                piece[i][j] = pieces[pieceNum][i][j];
            }
        }
        color = colors[pieceNum];
    }


    public void rotateLeft() {
        if (pieceNum == O) {
            return;
        }
        int x = 0;
        int y = 0;
        for (int i = 0; i < piece.length; i++) {
            y = piece[i][0];
            x = piece[i][1];
            piece[i][0] = -x;
            piece[i][1] = y;
        }
    }

    public void rotateRight() {
        if (pieceNum == O) {
            return;
        }
        int x = 0;
        int y = 0;
        for (int i = 0; i < piece.length; i++) {
            y = piece[i][0];
            x = piece[i][1];
            piece[i][0] = x;
            piece[i][1] = -y;
        }
    }

    public final int[][] getPiece() {
        return piece;
    }

    public final Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public int getPieceNum() {
        return pieceNum;
    }

    public void setPiece(int[][] p) {
        piece = new int[4][2];
        for (int i = 0; i < piece.length; i++) {
            for (int j = 0; j < piece[i].length; j++) {
                piece[i][j] = p[i][j];
            }
        }
    }


}

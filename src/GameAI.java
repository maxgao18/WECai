import java.awt.*;

public class GameAI {
    enum states {EMPTY, PLAYER1, PLAYER2, WALL}
    states [][] gameState = new states[17][17];
    int [] playerStart = new int []{8, 2};
    int [] opponentStart = new int []{8, 14};
    enum move {LEFT, RIGHT, UP, DOWN}

    private int findNumOpenSpots(Point p) {
        //TODO use BFS to find open spots
        return 0;
    }

    // Finds out if the current move resulted in a potential closing off of a new space on the board
    private boolean didCloseOffNewSpace (Point p, move m) {
        if (m == move.LEFT) {
            if (gameState[p.x-1][p.y] == states.EMPTY) {
                return false;
            }
        } else if (m == move.RIGHT) {
            if (gameState[p.x+1][p.y] == states.EMPTY) {
                return false;
            }
        } else if (m == move.UP) {
            if (gameState[p.x][p.y+1] == states.EMPTY) {
                return false;
            }
        } else if (m == move.DOWN) {
            if (gameState[p.x][p.y-1] == states.EMPTY) {
                return false;
            }
        }
        return true;
    }

    private int findScore(Point p1, Point p2, int player) {
        Point cPlayer = p1;
        if (player == 2) {
            cPlayer = p2;
        }
    }
}

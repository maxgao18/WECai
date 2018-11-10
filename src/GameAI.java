import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class GameAI {
    enum states {EMPTY, PLAYER1, PLAYER2, WALL}

    states[][] gameState = new states[17][17];
    int[] playerStart = new int[]{8, 2};
    int[] opponentStart = new int[]{8, 14};

    enum move {LEFT, RIGHT, UP, DOWN}

    int depth = 10;
    int winningScore = 10000;
    int losingScore = -10000;
    int tyingScore = 0;

    // Finds number of open spots in one block given a current open spot
    private int findNumOpenSpots(Point p) {
        boolean[][] visited = new boolean[17][17];
        Queue<Point> queue = new LinkedList<Point>();
        queue.add(p);

        int numEmpty = 0;
        while (!queue.isEmpty()) {
            Point cp = queue.poll();
            if (gameState[cp.x][cp.y] == states.EMPTY && !visited[cp.x][cp.y]) {
                numEmpty++;
                visited[cp.x][cp.y] = true;

                if (cp.x - 1 > 0) {
                    queue.add(new Point(cp.x - 1, cp.y));
                }
                if (cp.x + 1 < 16) {
                    queue.add(new Point(cp.x + 1, cp.y));
                }
                if (cp.y - 1 > 0) {
                    queue.add(new Point(cp.x, cp.y - 1));
                }
                if (cp.y + 1 < 16) {
                    queue.add(new Point(cp.x, cp.y + 1));
                }
            }
        }
        return numEmpty;
    }

    // Finds out if the current move resulted in a potential closing off of a new space on the board
    private boolean didCloseOffNewSpace(Point p, move m) {
        if (m == move.LEFT) {
            if (isEmpty(p.x - 1, p.y) &&
                    (isEmpty(p.x - 1, p.y - 1) || !isEmpty(p.x, p.y - 1)) &&
                    (isEmpty(p.x - 1, p.y + 1) || !isEmpty(p.x, p.y + 1))) {
                return false;
            }
        } else if (m == move.RIGHT) {
            if (isEmpty(p.x + 1, p.y) &&
                    (isEmpty(p.x + 1, p.y - 1) || !isEmpty(p.x, p.y - 1)) &&
                    (isEmpty(p.x + 1, p.y + 1) || !isEmpty(p.x, p.y + 1))) {
                return false;
            }
        } else if (m == move.UP) {
            if (isEmpty(p.x, p.y + 1) &&
                    (isEmpty(p.x - 1, p.y + 1) || !isEmpty(p.x - 1, p.y)) &&
                    (isEmpty(p.x + 1, p.y + 1) || !isEmpty(p.x + 1, p.y))) {
                return false;
            }
        } else if (m == move.DOWN) {
            if (isEmpty(p.x, p.y - 1) &&
                    (isEmpty(p.x - 1, p.y - 1) || !isEmpty(p.x - 1, p.y)) &&
                    (isEmpty(p.x + 1, p.y - 1) || !isEmpty(p.x + 1, p.y))) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmpty(int x, int y) {
        return gameState[x][y] == states.EMPTY;
    }

    // Find "score" of board by performing BFS
    private int findScore(Point p1, Point p2, int player) {
        Point cPlayer = p1;
        if (player == 2) {
            cPlayer = p2;
        }
    }

    private int minMax(int cPlayer, Point player1pos, Point player2pos, int scoreP1, int scoreP2) {

    }

}

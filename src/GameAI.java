import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class GameAI {
    enum states {EMPTY, PLAYER1, PLAYER2, WALL}
    states [][] gameState;
    Point playerStart = new Point(8, 2);
    Point opponentStart = new Point(8, 14);
    enum move {LEFT, RIGHT, UP, DOWN}

    int depth = 10;
    int winningScore = 10000;
    int losingScore = -winningScore;
    int tyingScore = 0;

    public GameAI() {
        gameState = new states[17][17];
        gameState[playerStart.x][playerStart.y] = states.PLAYER1;
        gameState[opponentStart.x][opponentStart.y] = states.PLAYER2;
        for (int a = 0; a < 17; a++) {
            gameState[0][a] = states.WALL;
            gameState[16][a] = states.WALL;
            gameState[a][0] = states.WALL;
            gameState[a][16] = states.WALL;
        }
    }

    // Finds number of open spots in one block given a current open spot
    private int findNumOpenSpots(Point p) {
        boolean [][] visited = new boolean[17][17];
        Queue <Point> queue = new LinkedList<Point>();
        queue.add(p);

        int numEmpty = 0;
        while (!queue.isEmpty()) {
            Point cp = queue.poll();
            if (gameState[cp.x][cp.y] == states.EMPTY && !visited[cp.x][cp.y]) {
                numEmpty++;
                visited[cp.x][cp.y] = true;

                if (cp.x-1 > 0) {
                    queue.add(new Point(cp.x-1, cp.y));
                }
                if (cp.x+1 < 16) {
                    queue.add(new Point(cp.x+1, cp.y));
                }
                if (cp.y-1 > 0) {
                    queue.add(new Point(cp.x, cp.y-1));
                }
                if (cp.y+1 < 16) {
                    queue.add(new Point(cp.x, cp.y+1));
                }
            }
        }
        return numEmpty;
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

    private Point[] getAdjacentPoints(Point p) {
        return new Point[]{
                new Point(p.x-1, p.y),
                new Point(p.x+1, p.y),
                new Point(p.x, p.y-1),
                new Point(p.x, p.y+1)
        };
    }


    // calls minmax for a specific move (passed through in playernewpos)
    private int minMaxHelper(int alpha, int beta, int cPlayerID, Point playerNewPos, Point player1pos, Point player2pos, int scoreP1, int scoreP2) {
        Point cPlayer = playerNewPos;
        Point oppPlayer = player1pos;
        if (cPlayerID == 1) {
            oppPlayer = player2pos;
        }
        if (gameState[playerNewPos.x][playerNewPos.y] == states.EMPTY) {

            if (didCloseOffNewSpace(playerNewPos)) {
                // calculate new opponent score (max of all possible opponent scores)
                int opponentNewScore = 0;
                Point[] adjPoints = getAdjacentPoints(oppPlayer);
                for (int i = 0; i < adjPoints.length; i++) {
                    int tempAdjScore = findNumOpenSpots(adjPoints[i]);
                    opponentNewScore = opponentNewScore > tempAdjScore ? opponentNewScore : tempAdjScore;
                }

                // set opponents new score
                if (cPlayerID == 1) {
                    scoreP2 = opponentNewScore;
                } else {
                    scoreP1 = opponentNewScore;
                }

            }
        }

        if (cPlayer.x == oppPlayer.x && cPlayer.y == oppPlayer.y) {
            return tyingScore;
        }
        return losingScore;
    }

    private int minMax(int alpha, int beta, int cPlayerID, Point player1pos, Point player2pos, int scoreP1, int scoreP2) {
        int cmin = 9999999;
        int cmax = -9999999;

        Point cplayerpos = player1pos;
        if (cPlayerID == 2) {
            cplayerpos = player2pos;
        }

        // call helper for all possible moves

        Point[] adjPoints = getAdjacentPoints(playerNewPos);
        for (int i = 0; i < adjPoints.length; i++) {

        }

        return losingScore;
    }

}

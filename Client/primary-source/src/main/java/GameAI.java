import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GameAI {
    States[][] gameState;
    Point playerStart = new Point(8, 2);
    Point opponentStart = new Point(8, 14);
    enum Moves {LEFT, RIGHT, UP, DOWN}
    Map<Moves, Integer> movesToIndex;

    int depth = 10;
    int winningScore = 10000;
    int losingScore = -winningScore;
    int tyingScore = 0;

    public GameAI() {
        gameState = new States[17][17];
        gameState[playerStart.x][playerStart.y] = States.PLAYER1;
        gameState[opponentStart.x][opponentStart.y] = States.PLAYER2;
        for (int a = 0; a < 17; a++) {
            gameState[0][a] = States.WALL;
            gameState[16][a] = States.WALL;
            gameState[a][0] = States.WALL;
            gameState[a][16] = States.WALL;
        }

        movesToIndex = new HashMap<>();
        movesToIndex.put(Moves.LEFT, 0);
        movesToIndex.put(Moves.RIGHT, 1);
        movesToIndex.put(Moves.UP, 2);
        movesToIndex.put(Moves.DOWN, 3);
    }

    // Finds number of open spots in one block given a current open spot
    private int findNumOpenSpots(Point p) {
        boolean[][] visited = new boolean[17][17];
        Queue<Point> queue = new LinkedList<Point>();
        queue.add(p);

        int numEmpty = 0;
        while (!queue.isEmpty()) {
            Point cp = queue.poll();
            if (gameState[cp.x][cp.y] == States.EMPTY && !visited[cp.x][cp.y]) {
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
    private boolean didCloseOffNewSpace(Point p, Moves m) {
        if (m == Moves.LEFT) {
            if (isEmpty(p.x - 1, p.y) &&
                    (isEmpty(p.x - 1, p.y - 1) || !isEmpty(p.x, p.y - 1)) &&
                    (isEmpty(p.x - 1, p.y + 1) || !isEmpty(p.x, p.y + 1))) {
                return false;
            }
        } else if (m == Moves.RIGHT) {
            if (isEmpty(p.x + 1, p.y) &&
                    (isEmpty(p.x + 1, p.y - 1) || !isEmpty(p.x, p.y - 1)) &&
                    (isEmpty(p.x + 1, p.y + 1) || !isEmpty(p.x, p.y + 1))) {
                return false;
            }
        } else if (m == Moves.UP) {
            if (isEmpty(p.x, p.y + 1) &&
                    (isEmpty(p.x - 1, p.y + 1) || !isEmpty(p.x - 1, p.y)) &&
                    (isEmpty(p.x + 1, p.y + 1) || !isEmpty(p.x + 1, p.y))) {
                return false;
            }
        } else if (m == Moves.DOWN) {
            if (isEmpty(p.x, p.y - 1) &&
                    (isEmpty(p.x - 1, p.y - 1) || !isEmpty(p.x - 1, p.y)) &&
                    (isEmpty(p.x + 1, p.y - 1) || !isEmpty(p.x + 1, p.y))) {
                return false;
            }
        }
        return true;
    }

    private Map<Moves, Point> getMoveToAdjacentPoints(Point p) {
        Map<Moves, Point> movePointMap = new HashMap<Moves, Point>();
        movePointMap.put(Moves.LEFT, new Point(p.x-1, p.y));
        movePointMap.put(Moves.RIGHT, new Point(p.x+1, p.y));
        movePointMap.put(Moves.DOWN, new Point(p.x, p.y-1));
        movePointMap.put(Moves.UP, new Point(p.x, p.y+1));
        return movePointMap;
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
    private int minMaxHelper(int alpha, int beta, int currDepth,
                             int cPlayerID, Moves move,
                             Point playerNewPos,
                             Point player1pos,
                             Point player2pos,
                             int scoreP1, int scoreP2) {
        Point oppPlayer = player1pos;
        if (cPlayerID == 1) {
            oppPlayer = player2pos;
        }

        if (gameState[playerNewPos.x][playerNewPos.y] == States.EMPTY) {
            if (didCloseOffNewSpace(playerNewPos, move)) {
                // calculate new opponent score (max of all possible opponent scores)
                int opponentNewScore = 0;
                int playerNewScore = findNumOpenSpots(playerNewPos)
                Point[] adjPoints = getAdjacentPoints(oppPlayer);
                for (int i = 0; i < adjPoints.length; i++) {
                    int tempAdjScore = findNumOpenSpots(adjPoints[i]);
                    opponentNewScore = opponentNewScore > tempAdjScore ? opponentNewScore : tempAdjScore;
                }

                // set opponents new score
                if (cPlayerID == 1) {
                    scoreP1 = playerNewScore;
                    scoreP2 = opponentNewScore;
                } else {
                    scoreP1 = opponentNewScore;
                    scoreP2 = playerNewScore;
                }
            }
        } else {
            // spot is a player
            if (playerNewPos.x == oppPlayer.x && playerNewPos.y == oppPlayer.y) {
                return tyingScore;
            }
            // spot is a losing point
            return losingScore;
        }

        Point newPlayer1pos = player1pos;
        Point newPlayer2pos = player2pos;
        int nextPlayerID = 1;
        // set pos of curr player and swap player
        if (cPlayerID == 1) {
            newPlayer1pos = playerNewPos;
            nextPlayerID = 2;
        } else {
            newPlayer2pos = playerNewPos;
        }

        if (cPlayerID == 1) {
            gameState[playerNewPos.x][playerNewPos.y] = States.PLAYER1;
        } else {
            gameState[playerNewPos.x][playerNewPos.y] = States.PLAYER2;
        }

        int nextScore = minMax(alpha, beta, currDepth-1, nextPlayerID, newPlayer1pos, newPlayer2pos, scoreP1, scoreP2);

        return nextScore;
    }

    private int minMax(int alpha, int beta, int currDepth, int cPlayerID, Point player1pos, Point player2pos, int scoreP1, int scoreP2) {
        int cmin = 9999999;
        int cmax = -9999999;

        Point cplayerpos = player1pos;
        if (cPlayerID == 2) {
            cplayerpos = player2pos;
        }

        // call helper for all possible moves

        Map<Moves, Point> movePointMap = getMoveToAdjacentPoints(cplayerpos);
        int []
        for (Map.Entry<Moves, Point> entry : movePointMap.entrySet()) {
            Moves move = entry.getKey();
            Point newPosition = entry.getValue();

        }

        if (cPlayer.x == oppPlayer.x && cPlayer.y == oppPlayer.y) {
            return tyingScore;
        }
        return losingScore;
    }

    private boolean isEmpty(int x, int y) {
        return gameState[x][y] == States.EMPTY;
    }


}

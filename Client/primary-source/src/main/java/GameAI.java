import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GameAI {
    public static GameAI gameAI;

    States[][] gameState;
    Point playerStart = new Point(8, 2);
    Point opponentStart = new Point(8, 14);
    Map<Moves, Integer> movesToIndex;
    Map<Integer, Moves> indexToMoves;

    int depth = 1;
    int winningScore = 10000;
    int losingScore = -10000;
    int tyingScore = 0;

    public GameAI(States[][] gameState) {
        gameAI = this;
        this.gameState = gameState;
        setMaps();
    }

    public static GameAI getInstance() {
        return gameAI;
    }

    public GameAI() {
        gameState = new States[17][17];
        for (int a = 0; a < 17; a++) {
            for (int b = 0; b < 17; b++) {
                gameState[a][b] = States.EMPTY;
            }
        }
        gameState[playerStart.x][playerStart.y] = States.PLAYER1;
        gameState[opponentStart.x][opponentStart.y] = States.PLAYER2;
        for (int a = 0; a < 17; a++) {
            gameState[0][a] = States.WALL;
            gameState[16][a] = States.WALL;
            gameState[a][0] = States.WALL;
            gameState[a][16] = States.WALL;
        }

        setMaps();
    }

    private void setMaps () {
        movesToIndex = new HashMap<Moves, Integer>();
        movesToIndex.put(Moves.LEFT, 0);
        movesToIndex.put(Moves.RIGHT, 1);
        movesToIndex.put(Moves.UP, 2);
        movesToIndex.put(Moves.DOWN, 3);

        indexToMoves = new HashMap<Integer, Moves>();
        indexToMoves.put(0, Moves.LEFT);
        indexToMoves.put(1, Moves.RIGHT);
        indexToMoves.put(2, Moves.UP);
        indexToMoves.put(3, Moves.DOWN);
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
                int playerNewScore = findNumOpenSpots(playerNewPos);
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
                System.out.println("players colliding");
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

        gameState[playerNewPos.x][playerNewPos.y] = States.EMPTY;

        return nextScore;
    }

    private int minMax(int alpha, int beta, int currDepth, int cPlayerID, Point player1pos, Point player2pos, int scoreP1, int scoreP2) {
        if (currDepth == 0) {
            if (cPlayerID == 1) {
                return scoreP1 - scoreP2;
            } else {
                return scoreP2 - scoreP1;
            }
        }

        Point cplayerpos = player1pos;
        if (cPlayerID == 2) {
            cplayerpos = player2pos;
        }

        // call helper for all possible moves

        int [] scores = new int[movesToIndex.size()];

        // alpha is max of min scores
        // beta is min of max scores

        Map<Moves, Point> movePointMap = getMoveToAdjacentPoints(cplayerpos);
        for (Map.Entry<Moves, Point> entry : movePointMap.entrySet()) {
            Moves move = entry.getKey();
            Point newPosition = entry.getValue();
            scores[movesToIndex.get(move)] = minMaxHelper(alpha, beta, currDepth, cPlayerID, move, newPosition, player1pos, player2pos, scoreP1, scoreP2);

            if (cPlayerID == 1) {
                //maximizer
                alpha = alpha > scores[movesToIndex.get(move)] ? alpha : scores[movesToIndex.get(move)];
                if (alpha > beta) {
                    return winningScore;
                }
            } else {
                //minimizer
                beta = beta < scores[movesToIndex.get(move)] ? beta : scores[movesToIndex.get(move)];
                if (alpha > beta) {
                    return losingScore;
                }
            }

        }

        // find max score (player 1)
        if (cPlayerID == 1) {
            int maxScore = scores[0];
            for (int score: scores) {
                maxScore = maxScore > score ? maxScore : score;
            }
            return maxScore;
        }

        // return min score (player 2)
        int minScore = scores[0];
        for (int score: scores) {
            minScore = minScore < score ? minScore : score;
        }
        return minScore;
    }

    private boolean isEmpty(int x, int y) {
        return gameState[x][y] == States.EMPTY;
    }

    public Moves getBestMove(Point player1pos, Point player2pos) {
        // alpha is max of min scores
        int alpha = losingScore;
        // beta is min of max scores
        int beta = winningScore;
        Moves bestMove = Moves.LEFT;
        int bestScore = losingScore;

        int scoreP2 = 0;
        Point[] adjPoints = getAdjacentPoints(player2pos);
        for (int i = 0; i < adjPoints.length; i++) {
            int tempAdjScore = findNumOpenSpots(adjPoints[i]);
            scoreP2 = scoreP2 > tempAdjScore ? scoreP2 : tempAdjScore;
        }

        Point left = new Point(player1pos.x-1, player1pos.y);
        int scoreP1 = findNumOpenSpots(left);

        int score = minMaxHelper(alpha, beta, depth, 1, Moves.LEFT, left, player1pos, player2pos, scoreP1, scoreP2);
        System.out.println(score);
        if (bestScore < score) {
            bestScore = score;
            bestMove = Moves.LEFT;
            alpha = bestScore;
        }

        Point right = new Point(player1pos.x+1, player1pos.y);
        scoreP1 = findNumOpenSpots(right);

        score = minMaxHelper(alpha, beta, depth, 1, Moves.RIGHT, right, player1pos, player2pos, scoreP1, scoreP2);
        if (bestScore < score) {
            bestScore = score;
            bestMove = Moves.RIGHT;
            alpha = bestScore;
        }

        Point up = new Point(player1pos.x, player1pos.y+1);
        scoreP1 = findNumOpenSpots(up);

        score = minMaxHelper(alpha, beta, depth, 1, Moves.UP, up, player1pos, player2pos, scoreP1, scoreP2);
        if (bestScore < score) {
            bestScore = score;
            bestMove = Moves.UP;
            alpha = bestScore;
        }

        Point down = new Point(player1pos.x, player1pos.y-1);
        scoreP1 = findNumOpenSpots(down);

        score = minMaxHelper(alpha, beta, depth, 1, Moves.DOWN, down, player1pos, player2pos, scoreP1, scoreP2);
        if (bestScore < score) {
            bestScore = score;
            bestMove = Moves.DOWN;
            alpha = bestScore;
        }

        return bestMove;
    }
}
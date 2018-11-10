import javax.json.Json;
import java.awt.*;
import java.net.URI;

public class ChatBot {

    private static String TEAM_ID = "our_team_id";

    private static String AUTHENTICATION_KEY = "random_key";

    private static String TYPE_REGISTRATION = "REGISTRATION";

    private static String TYPE_MOVE = "MOVE";

    public static void main(String[] args) throws Exception {
//        final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI("ws://35.183.103.104:8080/connect_dev"));
//        clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
//            public void handleMessage(String message) {
//                System.out.println("RECIEVED: " + message);
//            }
//        });
//
//        clientEndPoint.sendMessage(getMessage(TYPE_REGISTRATION, "", TEAM_ID));
//
//        while (true) {
//            Thread.sleep(3000);
//            clientEndPoint.sendMessage(getMessage(TYPE_MOVE, "DOWN", TEAM_ID));
//        }

        States[][] gameState = new States[17][17];

        Point playerStart = new Point(8, 2);
        Point opponentStart = new Point(8, 14);

        Point aipoint = playerStart;

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

        Point cpos = new Point(8, 14);
        for (int i = 0; i < 9; i++) {
            printArr(gameState);
            GameAI ai = new GameAI(gameState);
            Moves aimove = ai.getBestMove(aipoint, cpos);
            if (aimove == Moves.LEFT) {
                aipoint = new Point(aipoint.x -1, aipoint.y);
            }
            if (aimove == Moves.RIGHT) {
                aipoint = new Point(aipoint.x +1, aipoint.y);
            }
            if (aimove == Moves.UP) {
                aipoint = new Point(aipoint.x, aipoint.y+1);
            }
            if (aimove == Moves.DOWN) {
                aipoint = new Point(aipoint.x -1, aipoint.y-1);
            }
            cpos = new Point(cpos.x -1, cpos.y);

            gameState[aipoint.x][aipoint.y] = States.PLAYER1;
            gameState[cpos.x][cpos.y] = States.PLAYER2;
        }

    }

    private static void printArr(States [][] gameState) {
        System.out.println("--------------------------");
        for (int i = 16; i >= 0; i--) {
            for (int j = 0; j < 17; j++) {
                if (gameState[j][i] == States.EMPTY) {
                    System.out.print(" ");
                } else if (gameState[j][i] == States.PLAYER1) {
                    System.out.print("1");
                } else if (gameState[j][i] == States.PLAYER2) {
                    System.out.print("2");
                } else if (gameState[j][i] == States.WALL) {
                    System.out.print("W");
                }
            }
            System.out.println();
        }
    }

    /**
     * Create a json representation.
     *
     * @param message
     * @return
     */
    private static String getMessage(String type, String message, String team_id) {
        return Json.createObjectBuilder()
                .add("type", type)
                .add("message", message)
                .add("authenticationKey", AUTHENTICATION_KEY)
                .add("team_id", team_id)
                .build()
                .toString();
    }
}
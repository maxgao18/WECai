import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.awt.*;
import java.io.StringReader;
import java.net.URI;

public class ClientMain {

    private static String TEAM_ID = "1";

    private static String AUTHENTICATION_KEY = "random_key";

    private static String TYPE_REGISTRATION = "REGISTRATION";

    private static String TYPE_MOVE = "MOVE";

    static Printerface printyourface = new JSBoard();

    public static void main(String[] args) throws Exception {
        new ClientMain().run();
    }

    private ChatClientEndpoint clientEndPoint = clientEndpoint();

    public ChatClientEndpoint clientEndpoint() {
        try {
            ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI("ws://35.183.103.104:8080/connect_dev"));
            clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
                public void handleMessage(String s) {
                    try {
                        System.out.println((s.charAt(0) == '{') + "RECEIVED: " + s);
                        if (s.charAt(0) == '{') {
                            States[][] state = parseArr(s);
                            printyourface.writeToHTML(state);
                            States[][] copy = new States[17][17];
                            for (int i = 0; i < copy.length; i++) {
                                for (int j = 0; j < copy[i].length; j++) {
                                    copy[i][j] = state[i][j];
                                }
                            }
                            sendMove(callAI(copy));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            return clientEndPoint;
        } catch (Exception e) {
            return null;
        }
    }

    void sendMove(Moves move) {
        clientEndPoint.sendMessage(getMessage(TYPE_MOVE, move.toString(), TEAM_ID));
    }

    public Moves initMove() {
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
        GameAI ai = new GameAI(gameState);
        return ai.getBestMove(aipoint, cpos);
    }

    public Moves callAI(States[][] gameState) {
        Point aipoint = null, cpos = null;
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                if (gameState[i][j] == States.PLAYER1) {
                    aipoint = new Point(i, j);
                } else if (gameState[i][j] == States.PLAYER2) {
                    cpos = new Point(i, j);
                }
            }
        }

        GameAI ai = new GameAI(gameState);
        return ai.getBestMove(aipoint, cpos);
    }

    public void run() {
        try {
            clientEndPoint.sendMessage(getMessage(TYPE_REGISTRATION, "", TEAM_ID));
            sendMove(initMove());

            while (true) {
                Thread.sleep(3000);
                try {
                    clientEndPoint.sendMessage(getMessage(TYPE_MOVE, "DOWN", TEAM_ID));
                } catch (Exception e) {
                    e.printStackTrace();
                    clientEndPoint = clientEndpoint();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            run();
        }
    }

    private static void printStates(States[][] states) {
        System.out.print("[");
        for (States[] state : states) {
            System.out.print("{");
            for (States st : state) {
                System.out.print(st == null ? "null, " : st.toString() + ", ");
            }
            System.out.println("},");
        }
        System.out.println("]");
    }

    private States[][] parseArr(String s) {
        States[][] states = new States[17][17];
        //printStates(states);
        JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();

        System.out.println(jsonObject);
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[i].length; j++) {
                states[j][i] = getState(jsonObject.get(i + "," + j));
            }
        }
        int r = (int) (Math.random() * 15);
        states[r + 1][r + 1] = States.PLAYER1;
        int a = (int) (Math.random() * 15);
        while (a == r) {
            a = (int) (Math.random() * 15);
        }
        states[a + 1][a + 1] = States.PLAYER2;

        printStates(states);
        return states;
    }

    private States getState(JsonValue jv) {
        String s = jv.toString();
        int r = (int) (Math.random() * 3);
        switch (r) {
            case 0:
                s = "wall";
                break;
            case 1:
                s = "trail";
                break;
            case 2:
                s = "";
        }
        if ("wall".equals(s) || "trail".equals(s)) {
            return States.WALL;
        } else if ("".equals(s)) {
            return States.EMPTY;
        } else if (TEAM_ID.equals(s)) {
            return States.PLAYER1;
        } else {
            return States.PLAYER2;
        }
    }

    private String getMessage(String type, String message, String team_id) {
        return Json.createObjectBuilder()
                .add("type", type)
                .add("message", message)
                .add("authenticationKey", AUTHENTICATION_KEY)
                .add("team_id", team_id)
                .build()
                .toString();
    }
}
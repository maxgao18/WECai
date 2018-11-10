import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.io.StringReader;
import java.net.URI;

public class ClientMain {

    private static String TEAM_ID = "our_team_id";

    private static String AUTHENTICATION_KEY = "random_key";

    private static String TYPE_REGISTRATION = "REGISTRATION";

    private static String TYPE_MOVE = "MOVE";

    static Printerface printyourface = new JSBoard();

    public static void main(String[] args) throws Exception {
        new ClientMain().run();
    }

    public ChatClientEndpoint clientEndpoint() throws Exception {
        ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI("ws://35.183.103.104:8080/connect_dev"));
        clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
            public void handleMessage(String s) {
                try {
                    System.out.println((s.charAt(0) == '{') + "RECEIVED: " + s);
                    if (s.charAt(0) == '{') {
                        States[][] state = parseArr(s);
                        printyourface.writeToHTML(state);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return clientEndPoint;
    }

    public void run() {
        try {
            ChatClientEndpoint clientEndPoint = clientEndpoint();

            clientEndPoint.sendMessage(getMessage(TYPE_REGISTRATION, "", TEAM_ID));

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

    private void printStates(States[][] states) {
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
        printStates(states);
        return states;
    }

    private States getState(JsonValue jv) {
        String s = jv.toString();
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
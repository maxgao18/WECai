import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.net.URI;

public class ClientMain {

    private static String TEAM_ID = "our_team_id";

    private static String AUTHENTICATION_KEY = "random_key";

    private static String TYPE_REGISTRATION = "REGISTRATION";

    private static String TYPE_MOVE = "MOVE";

    static Printerface printyourface;

    public static void main(String[] args) throws Exception {
        new ClientMain().run();
    }

    public void run() {
        try {
            final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI("ws://35.183.103.104:8080/connect_dev"));
            clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
                public void handleMessage(String s) {
                    System.out.println("RECEIVED: " + s);
                    if (s.charAt(0) != '{') {
                        States[][] state = parseArr(s);
                        printyourface.writeToHTML(state);
                    }
                }
            });

            clientEndPoint.sendMessage(getMessage(TYPE_REGISTRATION, "", TEAM_ID));

            while (true) {
                Thread.sleep(3000);
                clientEndPoint.sendMessage(getMessage(TYPE_MOVE, "DOWN", TEAM_ID));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private States[][] parseArr(String s) {
        States[][] states = new States[17][17];
        JsonObject jsonObject = Json.createReader(new StringReader(s)).readObject();
        for (int i = 0; i < states.length; i++) {
            for (int j = 0; j < states[i].length; j++) {
                states[j][i] = getState(jsonObject.getString(i + "," + j));
            }
        }
        return states;
    }

    private States getState(String s) {
        if("wall".equals(s) || "trail".equals(s)) {
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
import javax.json.Json;
import java.net.URI;

public class ChatBot {

    private static String TEAM_ID = "our_team_id";

    private static String AUTHENTICATION_KEY = "random_key";

    private static String TYPE_REGISTRATION = "REGISTRATION";

    private static String TYPE_MOVE = "MOVE";

    public static void main(String[] args) throws Exception {
        final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(new URI("ws://35.183.103.104:8080/connect_dev"));
        clientEndPoint.addMessageHandler(new ChatClientEndpoint.MessageHandler() {
            public void handleMessage(String message) {
                System.out.println("RECIEVED: " + message);
            }
        });

        clientEndPoint.sendMessage(getMessage(TYPE_REGISTRATION, "", TEAM_ID));

        while (true) {
            Thread.sleep(3000);
            clientEndPoint.sendMessage(getMessage(TYPE_MOVE, "DOWN", TEAM_ID));
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
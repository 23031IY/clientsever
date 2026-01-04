package communication.WebSocket;

import com.google.gson.*;
import communication.message.LoginResultMessage;
import communication.message.MatchingResultMessage;
import control.ClientController;
import jakarta.websocket.*;

@ClientEndpoint
public class WebSocketEndpoint {

    private static Gson gson = new Gson();
    private static ClientController controller;

    // Controller を登録
    public static void setController(ClientController c) {
        controller = c;
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("[client] onOpen: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message) {

        System.out.println("[client] onMessage: " + message);

        JsonObject json = JsonParser
                .parseString(message)
                .getAsJsonObject();

        String type = json.get("type").getAsString();

        switch (type) {

            case "login_result":
                LoginResultMessage login =
                        gson.fromJson(message, LoginResultMessage.class);
                controller.handleLoginResult(login);
                break;

            case "match_result":
                MatchingResultMessage match =
                        gson.fromJson(message, MatchingResultMessage.class);
                controller.handleMatchingResult(match);
                break;

            default:
                System.out.println("unknown message type: " + type);
        }
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("[client] onError");
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("[client] onClose: " + session.getId());
    }
}
package communication;

import com.google.gson.*;
import communication.message.*;
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

            case "LOGIN_SUCCES","LOGIN_FAILURE":
                LoginResultMessage login =
                        gson.fromJson(message, LoginResultMessage.class);
                controller.handleLoginResult(login);
                break;

            case "RESISTER_SUCCES","RESISTER_FAILURE":
                SignUpResultMessage signUp =
                        gson.fromJson(message, SignUpResultMessage.class);
                controller.handleSignUpResult(signUp);
                break;

            case "MATCH_STATUS":
                MatchingResultMessage match =
                        gson.fromJson(message, MatchingResultMessage.class);
                controller.handleMatchingResult(match);
                break;

            case "LOGOUT_SUCCES":
                //あとで

            case "LOGOUT_FAILURE":
                //やる




            case "START":
                AppMessage msg = gson.fromJson(message, AppMessage.class);
                controller.updateBattleStatus(msg.players);

            case "BET":
                controller.showMessage("bet phase");
//あとで考える

            case "STATE":
                controller.showMessage("update state");
                AppMessage msg1 = gson.fromJson(message, AppMessage.class);
                controller.updateBattleStatus(msg1.players);

            case "ERROR":
                AppMessage msg2 = gson.fromJson(message, AppMessage.class);
                controller.showMessage("error"+msg2.message);

            case "ROLL":
                AppMessage msg3 = gson.fromJson(message, AppMessage.class);
                controller.showMessage("roll"+msg3.message);
//後でちゃんとやる




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
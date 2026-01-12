package communication;

import com.google.gson.Gson;
import communication.message.LoginReqMessage;
import communication.message.LogoutReqMessage;
import communication.message.MatchingReqMessage;
import communication.message.SignUpReqMessage;
import control.ClientController;
import jakarta.websocket.Session;

public class ClientCommunication {

    private Session session;
    private final Gson gson = new Gson();
    private final ClientController controller;

    public ClientCommunication(ClientController controller) {
        this.controller = controller;
    }

    void setSession(Session session) {
        this.session = session;
    }

    void handleClientMessage(String json) {
        controller.onClientServerMessage(json);
    }



    // ↓送信用メソッド

    public void send(String json) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(json);
        }
    }

    public void sendLoginRequest(LoginReqMessage loginMessage) {
        String msg = gson.toJson(loginMessage);
        send(msg);
    }

    public void sendSignUpRequest(SignUpReqMessage signUpReqMessage) {
        String msg = gson.toJson(signUpReqMessage);
        send(msg);
    }

    public void sendLogoutRequest() {
        LogoutReqMessage logoutReqMessage = new LogoutReqMessage();
        String msg = gson.toJson(logoutReqMessage);
        send(msg);
    }

    public void sendMatchRequest(MatchingReqMessage matchingReqMessage) {
        String msg = gson.toJson(matchingReqMessage);
        send(msg);
    }

}


package communication;

import com.google.gson.Gson;
import communication.message.AppMessage;
import control.ClientController;
import jakarta.websocket.Session;

public class ApplicationCommunication {

    private Session session;
    private final Gson gson = new Gson();
    private ClientController controller;

    public ApplicationCommunication() {
    }

    public void setClientController(ClientController controller) {
        this.controller = controller;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void handleAppMessage(String json) {
        if (controller != null) {
            AppMessage msg = gson.fromJson(json, AppMessage.class);
            controller.onApplicationServerMessage(msg);
        }
    }

    public void send(AppMessage msg) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(gson.toJson(msg));
        }
    }
}
package communication;

import com.google.gson.Gson;
import communication.message.AppMessage;
import control.ClientController;
import jakarta.websocket.Session;

public class ApplicationCommunication {

    private Session session;
    private final Gson gson = new Gson();
    private final ClientController controller;

    public ApplicationCommunication(ClientController controller) {
        this.controller = controller;
    }

    void setSession(Session session) {
        this.session = session;
    }

    void handleAppMessage(String json) {
        AppMessage msg = gson.fromJson(json, AppMessage.class);
        controller.onApplicationServerMessage(msg);
    }

    public void send(AppMessage msg) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote()
                    .sendText(gson.toJson(msg));
        }
    }
}

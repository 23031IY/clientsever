package communication;

import communication.ClientCommunication;
import jakarta.websocket.*;

@ClientEndpoint
public class ClientWebSocketEndpoint {

    private final ClientCommunication communication;

    public ClientWebSocketEndpoint(ClientCommunication communication) {
        this.communication = communication;
    }

    @OnOpen
    public void onOpen(Session session) {
        communication.setSession(session);
        System.out.println("[ClientServer] Connected");
    }

    @OnMessage
    public void onMessage(String json) {
        communication.handleClientMessage(json);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("[ClientServer] Closed: " + reason);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
}

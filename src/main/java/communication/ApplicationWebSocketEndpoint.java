package communication;

import communication.ApplicationCommunication;
import jakarta.websocket.*;

@ClientEndpoint
public class ApplicationWebSocketEndpoint {

    private final ApplicationCommunication communication;

    public ApplicationWebSocketEndpoint(ApplicationCommunication communication) {
        this.communication = communication;
    }

    @OnOpen
    public void onOpen(Session session) {
        communication.setSession(session);
        System.out.println("[ApplicationServer] Connected");
    }

    @OnMessage
    public void onMessage(String json) {
        communication.handleAppMessage(json);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("[ApplicationServer] Closed: " + reason);
    }

    @OnError
    public void onError(Session session, Throwable t) {
        t.printStackTrace();
    }
}

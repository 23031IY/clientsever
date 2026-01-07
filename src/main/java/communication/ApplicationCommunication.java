package communication;

import com.google.gson.Gson;
import communication.WebSocketEndpoint;
import communication.message.*;
import control.ClientController;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.io.IOException;
import java.net.URI;

public class ApplicationCommunication {

    private Session session;
    private WebSocketContainer container;
    private URI uri;
    private Gson gson = new Gson();
    private ClientController controller;

    /****************************************
     * コンストラクタ
     ****************************************/
    public ApplicationCommunication(String endpoint,
                                    ClientController controller) {
        this.controller = controller;
        container = ContainerProvider.getWebSocketContainer();
        uri = URI.create(endpoint);
        connect();
    }

    /****************************************
     * 接続
     ****************************************/
    private void connect() {
        try {
            WebSocketEndpoint.setController(controller);
            session = container.connectToServer(
                    new WebSocketEndpoint(), uri);
            System.out.println("[Application] connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    /****************************************
     * 共通送信
     ****************************************/
    private void send(String json) {
        if (!isConnected()) {
            System.out.println("[Application] not connected");
            return;
        }
        try {
            session.getBasicRemote().sendText(json);
            System.out.println("[Application] send: " + json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public void disconnect() throws IOException {
        if(!session.isOpen()) {
            session.close();
        }
    }


}

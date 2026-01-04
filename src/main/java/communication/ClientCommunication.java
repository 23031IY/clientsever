package communication;

import com.google.gson.Gson;
import communication.message.LoginInfoMessage;
import communication.message.MatchingReqMessage;
import communication.WebSocket.WebSocketEndpoint;
import control.ClientController;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;

import java.io.IOException;
import java.net.URI;

public class ClientCommunication {

    private Session session;
    private WebSocketContainer container;
    private URI uri;
    private Gson gson = new Gson();

    public ClientCommunication(String endpoint, ClientController controller) {
        container = ContainerProvider.getWebSocketContainer();
        uri = URI.create(endpoint);
        connect(controller);
    }

    /* 接続 */
    private boolean connect(ClientController controller) {
        try {
            WebSocketEndpoint.setController(controller);
            session = container.connectToServer(
                    new WebSocketEndpoint(), uri);
            System.out.println("[client] WebSocket connected");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* 接続状態確認 */
    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    /* 共通送信 */
    private void send(String json) {
        if (!isConnected()) {
            System.out.println("WebSocket is not connected");
            return;
        }
        try {
            session.getBasicRemote().sendText(json);
            System.out.println("[client] send: " + json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ログイン要求 */
    public void sendLoginRequest(LoginInfoMessage msg) {
        send(gson.toJson(msg));
    }

    /* マッチング要求 */
    public void sendMatchRequest() {
        MatchingReqMessage msg = new MatchingReqMessage();
        send(gson.toJson(msg));
    }
}

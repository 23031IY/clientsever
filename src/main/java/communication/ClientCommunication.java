package communication;

import com.google.gson.Gson;
import communication.message.*; // 以前のMessageクラス群
import control.ClientController;
import jakarta.websocket.Session;

public class ClientCommunication {

    private Session session;
    private final Gson gson = new Gson();
    private ClientController controller; // 後からセットする

    // コンストラクタは引数なしに変更
    public ClientCommunication() {
    }

    // 相互参照の解消用セッター
    public void setClientController(ClientController controller) {
        this.controller = controller;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void handleClientMessage(String json) {
        if (controller != null) {
            controller.onClientServerMessage(json);
        }
    }

    // ↓送信用メソッド

    private void send(String json) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(json);
        } else {
            System.err.println("送信エラー: セッションが確立されていません");
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

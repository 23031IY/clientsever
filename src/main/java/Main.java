import communication.ClientCommunication;
import communication.ApplicationCommunication;
import communication.ClientWebSocketEndpoint;
import communication.ApplicationWebSocketEndpoint;
import control.ClientController;
import doundary.Screen;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.net.URI;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {

    // サーバ設定 (ポートやパスは環境に合わせてください)
    private static final String CLIENT_SERVER_URI = "ws://10.17.147.5:8080/ChinchiroServer/ws";
    private static final String APP_SERVER_URI    = "ws://10.17.147.5:8082/GameServer/game";

    public static void main(String[] args) {
        // SwingのUI処理は専用のスレッド(EDT)で行うのが定石です
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. ウィンドウ(JFrame)の作成
                Screen.frame = new JFrame("チンチロリン クライアント");
                Screen.frame.setSize(800, 600);
                Screen.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Screen.frame.setLocationRelativeTo(null); // 画面中央

                // 2. Communicationクラスの生成
                ClientCommunication clientComm = new ClientCommunication();
                ApplicationCommunication appComm = new ApplicationCommunication();

                // 3. Controllerの生成 (初期画面：ログイン画面がセットされる)
                System.out.println("クライアントを起動します...");
                ClientController controller = new ClientController(clientComm, appComm);

                // 4. 相互参照の解決
                clientComm.setClientController(controller);
                appComm.setClientController(controller);

                // 5. ウィンドウを表示 (この時点でログイン画面が見えるはずです)
                Screen.frame.setVisible(true);

                // 6. WebSocket接続の確立（バックグラウンドで接続）
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();

                // 6-1. ユーザ管理サーバへの接続
                try {
                    ClientWebSocketEndpoint clientEndpoint = new ClientWebSocketEndpoint(clientComm);
                    Session clientSession = container.connectToServer(clientEndpoint, URI.create(CLIENT_SERVER_URI));
                    System.out.println("ユーザ管理サーバに接続完了");
                } catch (Exception e) {
                    System.err.println("ユーザ管理サーバ接続失敗: " + e.getMessage());
                    // 画面上にエラーダイアログを出すなどの処理を入れても良いです
                    controller.showMessage("サーバに接続できませんでした。\n" + e.getMessage());
                }

                // 6-2. ゲームサーバへの接続
                try {
                    ApplicationWebSocketEndpoint appEndpoint = new ApplicationWebSocketEndpoint(appComm);
                    Session appSession = container.connectToServer(appEndpoint, URI.create(APP_SERVER_URI));
                    System.out.println("ゲームサーバに接続完了");
                } catch (Exception e) {
                    System.out.println("ゲームサーバ未接続: " + e.getMessage());
                }

                // --- ここで初期化完了。あとはユーザのボタン操作待ちになります ---

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
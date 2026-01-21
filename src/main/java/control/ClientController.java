package control;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import communication.ApplicationCommunication;
import communication.ClientCommunication;
import communication.message.*;
import doundary.*; // パッケージ名はそのままでいきます　＞わかりました

import javax.swing.SwingUtilities;
import java.util.List;

public class ClientController {

    /* ネットワーク */
    public ClientCommunication clientCommunication;
    public ApplicationCommunication applicationCommunication;

    private Gson gson = new Gson();

    /* 現在表示中の画面 */
    protected Screen currentScreen;

    /* 全画面 */
    protected LoginScreen loginScreen;
    protected SignUpScreen signUpScreen;
    protected HomeScreen homeScreen;
    protected BattleScreen battleScreen;
    protected ResultScreen resultScreen;

    /****************************************
     * コンストラクタ
     ****************************************/
    public ClientController(ClientCommunication c, ApplicationCommunication a) {
        this.clientCommunication = c;
        this.applicationCommunication = a;

        // 各画面の生成
        // (Screen側で controller を受け取るコンストラクタになっている前提)
        loginScreen  = new LoginScreen(this);
        signUpScreen = new SignUpScreen(this);
        homeScreen   = new HomeScreen(this);
        battleScreen = new BattleScreen(this);
        resultScreen = new ResultScreen(this);

        // 初期画面
        currentScreen = loginScreen;
        loginScreen.showLoginScreen();
    }

    // --- 通信: Game Server (Application Server) からの通知 ---
    public void onApplicationServerMessage(AppMessage msg) {
        System.out.println("ApplicationServer: " + msg.type);

        // Swingの画面更新はEDTで行う
        SwingUtilities.invokeLater(() -> {
            switch (msg.type) {
                case START:
                    System.out.println("Game start!");
                    // バトル画面へ遷移し、初期状態を反映
                    transitionToBattleScreen();
                    updateBattleStatus(msg.players);
                    break;

                case BET:
                    System.out.println("Bet phase");
                    // 画面にメッセージを出す、あるいはBET入力を有効化する
                    battleScreen.showMessage("ベットしてください");
                    // 必要であれば battleScreen.enableBetButton(); などを実装して呼ぶ
                    break;

                case STATE:
                    System.out.println("Update state");
                    updateBattleStatus(msg.players);
                    break;

                case ERROR:
                    System.out.println("エラー: " + msg.message);
                    battleScreen.showMessage("エラー: " + msg.message);
                    break;

                case ROLL:
                    System.out.println("Roll phase: Player " + msg.playerId);
                    // 自分のターンならロールボタンを有効にするなどの処理
                    // ここでは簡易的にメッセージ表示のみ
                    battleScreen.showMessage("プレイヤー " + msg.playerId + " の番です");

                    // もし msg.playerId が自分ならボタンを有効化する判定を入れると良い
                    // if (msg.playerId == myId) battleScreen.setRollButtonEnabled(true);
                    break;

                case HAND:
                    System.out.println("Check hand");
                    // 役の結果などを表示
                    updateBattleStatus(msg.players); // 名前・バナナ等の表示更新（任意だが推奨）
                    battleScreen.showHand(msg);       // 役カットイン表示（BattleScreen側に実装済み）
                    break;

                case RESULT:
                    System.out.println("Game Set!");
                    // 結果画面へ遷移、またはバトル画面で結果ダイアログ
                    transitionToResultScreen();
                    resultScreen.showResult(msg);     // RESULTデータを画面に反映（ResultScreen側に実装済み）
                    break;
            }
        });
    }

    // --- 通信: User Management Server (Client Server) からの通知 ---
    public void onClientServerMessage(String message) {
        System.out.println("[client] onMessage: " + message);

        // JSONパース
        JsonObject json;
        try {
            json = JsonParser.parseString(message).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (!json.has("type")) return;
        String type = json.get("type").getAsString();

        SwingUtilities.invokeLater(() -> {
            switch (type) {
                // スペル修正: SUCCES -> SUCCESS
                case "LOGIN_SUCCESS":
                case "LOGIN_FAILURE": {
                    LoginResultMessage login = gson.fromJson(message, LoginResultMessage.class);
                    handleLoginResult(login);
                    break;
                }

                // スペル修正: RESISTER -> REGISTER, SUCCES -> SUCCESS
                case "REGISTER_SUCCESS":
                case "REGISTER_FAILURE": {
                    SignUpResultMessage signUp = gson.fromJson(message, SignUpResultMessage.class);
                    handleSignUpResult(signUp);
                    break;
                }

                case "MATCH_STATUS":
                    MatchingResultMessage match = gson.fromJson(message, MatchingResultMessage.class);
                    handleMatchingResult(match);
                    break;

                case "LOGOUT_SUCCESS":
                    System.out.println("ログアウト成功");
                    transitionToLoginScreen();
                    break;

                case "LOGOUT_FAILURE":
                    System.out.println("ログアウト失敗");
                    break;
            }
        });
    }

    /****************************************
     * 画面遷移
     ****************************************/
    public void transitionToLoginScreen() {
        currentScreen = loginScreen;
        loginScreen.showLoginScreen();
    }

    public void transitionToSignUpScreen() {
        currentScreen = signUpScreen;
        signUpScreen.showSignUpScreen();
    }

    public void transitionToHomeScreen() {
        currentScreen = homeScreen;
        homeScreen.showHomeScreen();
    }

    public void transitionToBattleScreen() {
        currentScreen = battleScreen;
        battleScreen.showBattleScreen();
    }

    public void transitionToResultScreen() {
        currentScreen = resultScreen;
        resultScreen.showResultScreen();
    }

    /****************************************
     * ログイン・登録
     ****************************************/
    public void sendLoginRequest(String id, String password) {
        LoginReqMessage msg = new LoginReqMessage(id, password);
        clientCommunication.sendLoginRequest(msg);
    }

    public void handleLoginResult(LoginResultMessage result) {
        if (result.result) {
            System.out.println("ログイン成功");
            transitionToHomeScreen();
        } else {
            System.out.println("ログイン失敗");
            currentScreen.showMessage("ログイン失敗: " + result.message);
        }
    }

    public void sendSignUpRequest(String id, String pass) {
        SignUpReqMessage msg = new SignUpReqMessage(id, pass);
        clientCommunication.sendSignUpRequest(msg);
    }

    public void handleSignUpResult(SignUpResultMessage result) {
        if (result.result) {
            System.out.println("登録成功");
            transitionToHomeScreen(); // またはログイン画面のまま「成功しました」と出すか
        } else {
            System.out.println("登録失敗");
            currentScreen.showMessage("登録失敗: " + result.message);
        }
    }

    /****************************************
     * ログアウト
     ****************************************/
    public void executeLogout() {
        clientCommunication.sendLogoutRequest();
        // サーバからの応答を待たずに画面遷移するか、応答で遷移するかは設計次第
        // ここでは応答(LOGOUT_SUCCESS)を待つ形にします
    }

    /****************************************
     * マッチング開始
     ****************************************/
    public void notifyStartMatching() {
        MatchingReqMessage msg = new MatchingReqMessage();
        // ユーザIDが必要ならセットする (Login成功時にControllerにIDを保存しておく必要あり)
        // msg.id = this.myUserId;
        clientCommunication.sendMatchRequest(msg);
    }

    public void handleMatchingResult(MatchingResultMessage result) {
        if (result.success) {
            System.out.println("マッチング状態: " + result.errorMessage); // WAITINGなど
            // マッチング完了(MATCHED)なのか待機中(WAITING)なのかで処理を分ける
            if ("MATCHED".equals(result.errorMessage)) {
                // transitionToBattleScreen(); // STARTメッセージで遷移するならここは不要
            } else {
                homeScreen.showMessage("マッチング待機中...");
            }
        } else {
            System.out.println("マッチング失敗");
            currentScreen.showMessage("マッチング失敗: " + result.errorMessage);
        }
    }

    /****************************************
     * バトル関連
     ****************************************/

    // BET用の通信
    public void sendBattleInfo(int betBanana) {
        AppMessage msg = new AppMessage();
        msg.betBananas = betBanana;
        msg.type = AppMessage.Type.BET; // valueOfを使わず直接指定でOK
        applicationCommunication.send(msg);
    }

    // ROLL用の通信 (メソッド名が被っていますが、引数違いなのでOK)
    public void sendBattleInfo() {
        AppMessage msg = new AppMessage();
        msg.type = AppMessage.Type.ROLL;
        applicationCommunication.send(msg);
    }

    // 画面更新
    public void updateBattleStatus(List<AppMessage.PlayerState> players) {
        if (players != null) {
            battleScreen.updateScreen(players);
        }
    }

    // サイコロ演出など
    public void showRollDisplay(int playerId) {
        System.out.println("ROLL PHASE : " + playerId);
        // battleScreen.animateDice(playerId); // 必要ならメソッド追加
    }

    public void showMessage(String message) {
        currentScreen.showMessage(message);
    }
}

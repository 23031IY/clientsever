package control;

import communication.ClientCommunication;
import communication.message.LoginInfoMessage;
import communication.message.LoginResultMessage;
import jakarta.websocket.Session;
import communication.message.MatchingResultMessage;
import doundary.*;

public class ClientController {

    /* ネットワーク */
    public ClientCommunication clientCommunication;

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
    public ClientController(ClientCommunication network) {
        this.clientCommunication = network;

        // 各画面の生成
        loginScreen  = new LoginScreen(this);
        signUpScreen = new SignUpScreen(this);
        homeScreen   = new HomeScreen(this);
        battleScreen = new BattleScreen(this);
        resultScreen = new ResultScreen(this);

        // 初期画面
        currentScreen = loginScreen;
        loginScreen.showLoginScreen();
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

    public void sendLoginRequest(String id, String password) {

        LoginInfoMessage loginMessage =
                new LoginInfoMessage(id, password);

        clientCommunication.sendLoginRequest(loginMessage);
    }

    public void handleLoginResult(LoginResultMessage result) {

        if (result.success) {
            System.out.println("ログイン成功");
            // ホーム画面へ
            transitionToHomeScreen();

        } else {
            System.out.println("ログイン失敗");

            currentScreen.showMessage(
                    "ログイン失敗: " + result.errorMessage);
        }
    }


    /****************************************
     * ログアウト
     ****************************************/
    public void executeLogout() {
    }

    /****************************************
     * マッチング開始
     ****************************************/
    public void notifyStartMatching() {
        clientCommunication.sendMatchRequest();

    }

    public void handleMatchingResult(MatchingResultMessage result) {

        if (result.success) {
            System.out.println("マッチング成功");
            transitionToBattleScreen();
        } else {
            System.out.println("マッチング失敗");
            currentScreen.showMessage(
                    "マッチング失敗: " + result.errorMessage);
        }
    }


    /****************************************
     * バトル関連
     ****************************************/
    public void sendBattleInfo() {
    }

    public void updateBattleStatus() {
    }

    public void handleScreenTransition() {
    }
}

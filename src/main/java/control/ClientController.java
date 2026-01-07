package control;

import communication.ApplicationCommunication;
import communication.ClientCommunication;
import communication.message.*;
import doundary.*;

import java.util.List;

public class ClientController {

    private String id;

    /* ネットワーク */
    public ClientCommunication clientCommunication;
    public ApplicationCommunication applicationCommunication;

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

        LoginReqMessage loginMessage =
                new LoginReqMessage(id, password);

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

    public void sendSignUpRequest(String id, String pass) {
        SignUpReqMessage msg = new SignUpReqMessage(id, pass);
        clientCommunication.sendSignUpRequest(msg);
    }

    public void handleSignUpResult(SignUpResultMessage result) {

        if (result.success) {
            System.out.println("登録成功");
            // ホーム画面へ
            transitionToHomeScreen();

        } else {
            System.out.println("登録失敗");

            currentScreen.showMessage(
                    "登録失敗: " + result.errorMessage);
        }
    }


    /****************************************
     * ログアウト
     ****************************************/
    public void executeLogout() {
        clientCommunication.sendLogoutRequest();
        transitionToLoginScreen();
    }

    /****************************************
     * マッチング開始
     ****************************************/
    public void notifyStartMatching() {
        MatchingReqMessage msg = new MatchingReqMessage();
        clientCommunication.sendMatchRequest(msg);
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
    public void sendBattleInfo(int betBanana) {
        AppMessage msg = new AppMessage();
        msg.betBananas = betBanana;
        msg.type = AppMessage.Type.valueOf("BET");

        applicationCommunication.send(msg);
    }

    public void updateBattleStatus(List<AppMessage.PlayerState> players) {
        battleScreen.updateScreen(players);
    }

    public void handleScreenTransition() {
    }

    public void showMessage(String message) {
        currentScreen.showMessage(message);
    }

}

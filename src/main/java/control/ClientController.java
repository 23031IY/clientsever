package control;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import communication.ApplicationCommunication;
import communication.ClientCommunication;
import communication.message.*;
import doundary.*;

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
        loginScreen  = new LoginScreen(this);
        signUpScreen = new SignUpScreen(this);
        homeScreen   = new HomeScreen(this);
        battleScreen = new BattleScreen(this);
        resultScreen = new ResultScreen(this);

        // 初期画面
        currentScreen = loginScreen;
        loginScreen.showLoginScreen();
    }

    // 通信
    /* Application Server からの通知 */
    public void onApplicationServerMessage(AppMessage msg) {
        System.out.println("ApplicationServer: " + msg);
        // 業務処理
        switch (msg.type) {

            case HELLO:
                System.out.println("ApplicationServer 接続完了");
                break;


        }
    }

    // Client管理サーバから（JSON文字列）
    public void onClientServerMessage(String message) {

        System.out.println("[client] onMessage: " + message);

        JsonObject json = JsonParser
                .parseString(message)
                .getAsJsonObject();
        String type = json.get("type").getAsString();
        // 業務処理
        switch (type) {
            case "LOGIN_SUCCES":
            case "LOGIN_FAILURE": {
                LoginResultMessage login =
                        gson.fromJson(message, LoginResultMessage.class);
                handleLoginResult(login);
                break;
            }

            case "RESISTER_SUCCES":
            case "RESISTER_FAILURE": {
                SignUpResultMessage signUp =
                        gson.fromJson(message, SignUpResultMessage.class);
                handleSignUpResult(signUp);
                break;
            }
            case "MATCH_STATUS":
                MatchingResultMessage match =
                        gson.fromJson(message, MatchingResultMessage.class);
                handleMatchingResult(match);
                break;

            case "LOGOUT_SUCCES":
                //あとで

            case "LOGOUT_FAILURE":
                //やる



        }



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

        LoginReqMessage msg =
                new LoginReqMessage(id, password);

        clientCommunication.sendLoginRequest(msg);
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

    // BET用の通信
    public void sendBattleInfo(int betBanana) {
        AppMessage msg = new AppMessage();
        msg.betBananas = betBanana;
        msg.type = AppMessage.Type.valueOf("BET");
        applicationCommunication.send(msg);
    }

    public void showRollDisplay(int playerId){
        System.out.println("ROLL PHASE : "+playerId);
        //　int playerId番目のプレイヤのロール
    }


    // ROLL用の通信
    public void sendBattleInfo(){
        AppMessage msg = new AppMessage();
        msg.type = AppMessage.Type.valueOf("ROLL");
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

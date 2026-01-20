package test;

import communication.ApplicationCommunication;
import communication.ClientCommunication;
import communication.message.LoginResultMessage;
import communication.message.SignUpResultMessage;
import communication.message.MatchingResultMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.JFrame;
import doundary.Screen;

import static org.junit.jupiter.api.Assertions.*;


public class ClientControllerTest {

    private dummy controller;

    @BeforeEach
    void setUp() {
        if (Screen.frame == null) {
            Screen.frame = new JFrame();
        }

        controller = new dummy(new ClientCommunication(), new ApplicationCommunication());
    }

    //ログイン成功
    @Test
    void testLoginSuccess() {
        controller.sendLoginRequest("user1", "ok");
        assertTrue(controller.isLastProcessSuccess(), "ログイン成功時にフラグがtrueになること");
    }

    //ログイン失敗
    @Test
    void testLoginFailure() {
        controller.sendLoginRequest("user1", "ng");
        assertFalse(controller.isLastProcessSuccess(), "ログイン失敗時にフラグがfalseになること");
    }

    //サインアップ重複
    @Test
    void testSignUpTaken() {
        controller.sendSignUpRequest("taken", "pass");
        assertFalse(controller.isLastProcessSuccess());
    }

    //サインアップ成功
    @Test
    void testSignUpSuccess() {
        controller.sendSignUpRequest("newuser", "pass");
        assertTrue(controller.isLastProcessSuccess());
    }

    //マッチング開始
    @Test
    void testMatching() {
        controller.notifyStartMatching();
        assertTrue(controller.isLastProcessSuccess());
    }
}



class dummy extends control.ClientController {
    // 処理結果を保存する変数
    private boolean lastProcessSuccess;

    public dummy(ClientCommunication c, ApplicationCommunication a) {
        super(c, a);
        loginScreen = null;
        signUpScreen = null;
        homeScreen = null;
        battleScreen = null;
        currentScreen = null;
    }

    public boolean isLastProcessSuccess() {
        return lastProcessSuccess;
    }


    @Override
    public void handleLoginResult(LoginResultMessage res) {
        this.lastProcessSuccess = res.result;

    }

    @Override
    public void handleSignUpResult(SignUpResultMessage res) {
        this.lastProcessSuccess = res.result;
    }

    @Override
    public void handleMatchingResult(MatchingResultMessage res) {
        this.lastProcessSuccess = res.success;
    }


    @Override public void transitionToLoginScreen() {}
    @Override public void transitionToSignUpScreen() {}
    @Override public void transitionToHomeScreen() {}
    @Override public void transitionToBattleScreen() {}
    @Override public void transitionToResultScreen() {}

    // --- 送信シミュレーション ---
    @Override
    public void sendLoginRequest(String id, String password) {
        LoginResultMessage res = new LoginResultMessage();
        res.result = "ok".equals(password);
        handleLoginResult(res);
    }

    @Override
    public void sendSignUpRequest(String id, String pass) {
        SignUpResultMessage res = new SignUpResultMessage();
        res.result = !"taken".equalsIgnoreCase(id);
        handleSignUpResult(res);
    }

    @Override
    public void notifyStartMatching() {
        MatchingResultMessage res = new MatchingResultMessage();
        res.success = true;
        handleMatchingResult(res);
    }

    @Override public void executeLogout() {}
}
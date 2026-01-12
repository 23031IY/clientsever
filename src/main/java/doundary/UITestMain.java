package doundary;

import control.ClientController;

import javax.swing.*;
import java.awt.*;

import communication.ClientCommunication;
import communication.ApplicationCommunication;
import communication.message.LoginResultMessage;
import communication.message.MatchingResultMessage;
import communication.message.SignUpResultMessage;

/**
 * 画面だけの動作確認用 Main
 * 通信は「成功/失敗が返ってきたことにして」Controller のハンドラを呼ぶ。
 */
public class UITestMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Screen.frame が null だと switchScreen で落ちるので、先に用意
            Screen.frame = new JFrame("チンチロリン（UIテスト）");
            Screen.frame.setSize(800, 600);
            Screen.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Screen.frame.setLocationRelativeTo(null);

            // 通信クラスは使わない（nullでOK）
            ClientCommunication dummyClientComm = null;
            ApplicationCommunication dummyAppComm = null;

            // UIテスト用 Controller を起動（コンストラクタ内で Login画面が表示される）
            new DummyClientController(dummyClientComm, dummyAppComm);
        });
    }

    /**
     * 通信部分をダミー化した Controller
     * 画面から呼ばれる sendXXX / notifyStartMatching / executeLogout を上書きして
     * 「サーバが返した」ことにして handler を呼ぶ。
     */
    static class DummyClientController extends ClientController {

        public DummyClientController(ClientCommunication c, ApplicationCommunication a) {
            super(c, a);
        }

        @Override
        public void sendLoginRequest(String id, String password) {
            // 0.8秒後にログイン結果が返ったことにする
            Timer t = new Timer(800, e -> {
                LoginResultMessage res = new LoginResultMessage();

                // テストルール：password が "ok" なら成功、それ以外は失敗
                if ("ok".equals(password)) {
                    res.type = "LOGIN_SUCCES";
                    res.success = true;
                    res.errorMessage = null;
                } else {
                    res.type = "LOGIN_FAILURE";
                    res.success = false;
                    res.errorMessage = "パスワードが違います（テスト）";
                }

                handleLoginResult(res);
            });
            t.setRepeats(false);
            t.start();
        }

        @Override
        public void sendSignUpRequest(String id, String pass) {
            // 0.8秒後に登録結果が返ったことにする
            Timer t = new Timer(800, e -> {
                SignUpResultMessage res = new SignUpResultMessage();

                // テストルール：id が "taken" なら失敗、それ以外は成功
                if ("taken".equalsIgnoreCase(id)) {
                    res.type = "RESISTER_FAILURE";
                    res.success = false;
                    res.errorMessage = "そのユーザ名は使用済みです（テスト）";
                } else {
                    res.type = "RESISTER_SUCCES";
                    res.success = true;
                    res.errorMessage = null;
                }

                handleSignUpResult(res);
            });
            t.setRepeats(false);
            t.start();
        }

        @Override
        public void notifyStartMatching() {
            // 1.0秒後にマッチング結果が返ったことにする
            Timer t = new Timer(1000, e -> {
                MatchingResultMessage res = new MatchingResultMessage();
                res.type = "MATCH_STATUS";

                // テストルール：常に成功にして Battle へ（失敗を見たいなら false にする）
                res.success = true;
                res.errorMessage = null;

                handleMatchingResult(res);
            });
            t.setRepeats(false);
            t.start();
        }

        @Override
        public void executeLogout() {
            // 通信せずにログイン画面へ戻す（UIテスト用）
            transitionToLoginScreen();
        }
    }
}

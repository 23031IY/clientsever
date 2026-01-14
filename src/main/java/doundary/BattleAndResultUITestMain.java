package doundary;

import communication.ApplicationCommunication;
import communication.ClientCommunication;
import communication.message.AppMessage;
import control.ClientController;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BattleScreen / ResultScreen のUI動作確認用 Main。
 * 通信は行わず、Timerで疑似的にサーバ通知を発生させる。
 */
public class BattleAndResultUITestMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // switchScreen が参照するフレームを準備
            Screen.frame = new JFrame("チンチロリン（Battle/Result UIテスト）");
            Screen.frame.setSize(860, 680);
            Screen.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Screen.frame.setLocationRelativeTo(null);
            Screen.frame.setVisible(true);

            // 通信は使わないので null
            ClientCommunication dummyClientComm = null;
            ApplicationCommunication dummyAppComm = null;

            DummyController controller = new DummyController(dummyClientComm, dummyAppComm);

            BattleScreen battle = new BattleScreen(controller);
            ResultScreen result = new ResultScreen(controller);
            controller.attachScreens(battle, result);

            // まずバトル画面表示
            battle.showBattleScreen();

            // シナリオ開始
            controller.runScenario();
        });
    }

    /**
     * 通信をダミー化した Controller。
     * Battle/Result 画面が呼ぶメソッドを最小限実装し、
     * Timerで疑似的に「サーバから通知が来た」ことにする。
     */
    static class DummyController extends ClientController {

        private BattleScreen battleScreen;
        private ResultScreen resultScreen;

        private int remaining = 20;
        private final List<AppMessage.PlayerState> players = new ArrayList<>();

        DummyController(ClientCommunication c, ApplicationCommunication a) {
            super(c, a);

            // 初期プレイヤー（4人）
            players.add(ps(1, "aaa", 100, true, 0));
            players.add(ps(2, "bbb", 100, false, 0));
            players.add(ps(3, "ccc", 100, false, 0));
            players.add(ps(4, "ddd", 100, false, 0));
        }

        void attachScreens(BattleScreen battle, ResultScreen result) {
            this.battleScreen = battle;
            this.resultScreen = result;
        }

        void runScenario() {
            // 0.2秒後：初回描画（players + timer）
            Timer t0 = new Timer(200, e -> {
                battleScreen.updateScreen(players);
                battleScreen.showRemainingTime(remaining);
            });
            t0.setRepeats(false);
            t0.start();

            // 1.0秒後：BETフェーズ開始
            Timer t1 = new Timer(1000, e -> battleScreen.showBetUI());
            t1.setRepeats(false);
            t1.start();

            // 2.0秒後：BET送信したことにする（自分=playerId 1）
            Timer t2 = new Timer(2000, e -> sendBattleInfo(10));
            t2.setRepeats(false);
            t2.start();

            // 3.0秒後：ROLLフェーズ開始
            Timer t3 = new Timer(3000, e -> battleScreen.showRollUI());
            t3.setRepeats(false);
            t3.start();

            // 4.0秒後：ROLLしたことにする（HAND通知も発生）
            Timer t4 = new Timer(4000, e -> sendBattleInfo());
            t4.setRepeats(false);
            t4.start();

            // 7.0秒後：結果画面へ遷移して表示
            Timer t5 = new Timer(7000, e -> {
                AppMessage resultMsg = new AppMessage();
                resultMsg.players = new ArrayList<>(players);

                resultScreen.showResultScreen();
                resultScreen.showResult(resultMsg);
            });
            t5.setRepeats(false);
            t5.start();

            // カウントダウン（毎秒）
            Timer countdown = new Timer(1000, e -> {
                remaining--;
                battleScreen.showRemainingTime(remaining);
                if (remaining <= 0) {
                    ((Timer) e.getSource()).stop();
                }
            });
            countdown.start();
        }

        /**
         * ROLL押下（引数なし）を受けたことにする。
         * 0.6秒後に HAND 通知が来た想定で BattleScreen.showHand(msg) を呼ぶ。
         */
        public void sendBattleInfo() {
            Timer t = new Timer(600, e -> {
                // 勝ち負けを適当に反映（例）
                replacePlayer(1, "aaa", 130, true, 10);
                replacePlayer(2, "bbb",  90, false, 10);
                replacePlayer(3, "ccc", 100, false, 10);
                replacePlayer(4, "ddd",  80, false, 10);

                // プレイヤー表示更新
                battleScreen.updateScreen(players);

                // HAND通知を生成（handName + playerId + players）
                AppMessage handMsg = new AppMessage();
                handMsg.handName = "テナガザル";
                handMsg.playerId = 1; // 成立プレイヤー
                handMsg.players = new ArrayList<>(players);

                // 役表示
                battleScreen.showHand(handMsg);
            });
            t.setRepeats(false);
            t.start();
        }

        /**
         * BET押下（引数あり）を受けたことにする。
         */
        public void sendBattleInfo(int betBanana) {
            Timer t = new Timer(400, e -> {
                // 自分(playerId=1)のBetを更新
                for (int i = 0; i < players.size(); i++) {
                    AppMessage.PlayerState p = players.get(i);
                    if (p.playerId == 1) {
                        players.set(i, ps(p.playerId, p.name, p.ownedBananas, p.dealer, betBanana));
                        break;
                    }
                }
                battleScreen.updateScreen(players);
            });
            t.setRepeats(false);
            t.start();
        }

        /**
         * ResultScreenの「ホームへ戻る」押下先。
         * HomeScreenが無いので、テストでは Battle に戻す。
         */
        public void transitionToHomeScreen() {
            battleScreen.showBattleScreen();
            battleScreen.updateScreen(players);
        }

        // ===== ヘルパ =====
        private void replacePlayer(int id, String name, int bananas, boolean dealer, int bet) {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).playerId == id) {
                    players.set(i, ps(id, name, bananas, dealer, bet));
                    return;
                }
            }
            players.add(ps(id, name, bananas, dealer, bet));
        }

        private static AppMessage.PlayerState ps(int id, String name, int bananas, boolean dealer, int bet) {
            AppMessage.PlayerState p = new AppMessage.PlayerState();
            p.playerId = id;
            p.name = name;
            p.ownedBananas = bananas;
            p.dealer = dealer;
            p.currentBetBananas = bet;
            return p;
        }
    }
}

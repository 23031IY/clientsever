package doundary;

import control.ClientController;

import java.util.List;

public class BattleScreen extends Screen {

    public BattleScreen(ClientController controller) {
        super(controller);
    }

    public void onRollDiceButtonPressed() {
        controller.sendBattleInfo();
    }

    public void showRemainingTime() {
        System.out.println("残り時間を表示");
    }

    public void updateScreen(List<Integer> ownedBanana, int bossNumber) {
        System.out.println("所持バナナ: " + ownedBanana);
        System.out.println("ボス番号: " + bossNumber);
    }

    public void showBattleScreen() {
        System.out.println("=== バトル画面 ===");
    }
}

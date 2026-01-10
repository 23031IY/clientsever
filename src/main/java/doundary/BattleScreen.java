package doundary;

import communication.message.AppMessage;
import control.ClientController;

import java.util.List;

public class BattleScreen extends Screen {

    public BattleScreen(ClientController controller) {
        super(controller);
    }

    public void onRollDiceButtonPressed() {
        controller.sendBattleInfo();
    }

    public void bet(int betBanana){
        controller.sendBattleInfo(betBanana);
    }

    public void showRemainingTime(int timer) {
        System.out.println("残り時間を表示"+timer);
    }


    public void updateScreen(List<AppMessage.PlayerState> playerStates) {
        for (AppMessage.PlayerState ps : playerStates) {
            System.out.printf(
                    "[ID:%d, Name:%s, Bananas:%d, Dealer:%b, Bet:%d]%n",
                    ps.playerId,
                    ps.name,
                    ps.ownedBananas,
                    ps.dealer,
                    ps.currentBetBananas
            );
        }
    }

    public void showBattleScreen() {
        System.out.println("=== バトル画面 ===");
    }
}

package doundary;

import communication.message.AppMessage;
import control.ClientController;

import java.util.Comparator;


public class ResultScreen extends Screen {

    public ResultScreen(ClientController controller) {
        super(controller);
    }

    public void showResult(AppMessage msg) {

        System.out.println("=== GAME RESULT ===");

        if (msg.players == null) return;

        msg.players.stream()
                .sorted(Comparator.comparingInt(
                        (AppMessage.PlayerState p) -> p.ownedBananas
                ).reversed())
                .forEachOrdered(p ->
                        System.out.println(
                                p.name + " : " + p.ownedBananas + " bananas"
                        )
                );
    }

    public void onReturnHomeButtonPressed() {
        controller.transitionToHomeScreen();
    }

    public void showResultScreen() {
        System.out.println("=== 結果画面 ===");
    }
}


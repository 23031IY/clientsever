package doundary;

import control.ClientController;

public class HomeScreen extends Screen {

    public HomeScreen(ClientController controller) {
        super(controller);
    }

    public void onMatchButtonPressed() {
        controller.notifyStartMatching();
        showMessage("マッチング中...");
    }

    public void onLogoutButtonPressed() {
        controller.executeLogout();
    }

    public void showHomeScreen() {
        System.out.println("=== ホーム画面 ===");
    }
}


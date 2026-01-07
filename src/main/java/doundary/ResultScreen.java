package doundary;

import control.ClientController;

public class ResultScreen extends Screen {

    public ResultScreen(ClientController controller) {
        super(controller);
    }

    //HomeScreenに戻るメソッド

    public void showResultScreen() {
        System.out.println("=== 結果画面 ===");
    }
}


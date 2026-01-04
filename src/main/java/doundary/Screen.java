package doundary;

import control.ClientController;

public abstract class Screen {

    protected ClientController controller;

    public Screen(ClientController controller) {
        this.controller = controller;
    }

    // 画面共通のメッセージ表示
    public void showMessage(String message) {
        System.out.println("[MESSAGE] " + message);
    }
}

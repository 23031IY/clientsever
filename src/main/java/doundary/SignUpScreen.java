package doundary;

import control.ClientController;

public class SignUpScreen extends Screen {

    public SignUpScreen(ClientController controller) {
        super(controller);
    }

    public void onSignUpButtonPressed(String id, String pass) {

        if (!validateInput(id) || !validateInput(pass)) {
            showMessage("入力が不正です");
            return;
        }

        controller.sendSignUpRequest(id, pass);
    }

    public boolean validateInput(String input) {
        return input != null && input.length() >= 3;
    }

    public void showSignUpScreen() {
        System.out.println("=== 新規登録画面 ===");
    }
}

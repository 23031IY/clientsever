package doundary;

import control.ClientController;

public class LoginScreen extends Screen {

    public LoginScreen(ClientController controller) {
        super(controller);
    }

    public void onLoginButtonPressed(String id, String pass) {

        if (!validateInput(id) || !validateInput(pass)) {
            showMessage("ID または パスワードが不正です");
            return;
        }
        controller.sendLoginRequest(id, pass);
        }

    public void onSignUpButtonPressed() {
        controller.transitionToSignUpScreen();
    }

    public boolean validateInput(String input) {
        return input != null && input.length() >= 3;
    }

    public void showLoginScreen() {
        System.out.println("=== ログイン画面 ===");
    }
}


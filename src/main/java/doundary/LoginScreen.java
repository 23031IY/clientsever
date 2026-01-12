package doundary;

import control.ClientController;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends Screen {

    private JTextField loginIdField;
    private JPasswordField loginPassField;

    private JTextField signupIdField;
    private JPasswordField signupPassField;

    private JLabel messageLabel;

    public LoginScreen(ClientController controller) {
        super(controller);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("チンチロリン", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(255, 204, 0));
        add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("ログイン", createLoginPanel());
        tabs.add("新規登録", createSignupPanel());

        add(tabs, BorderLayout.CENTER);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        add(messageLabel, BorderLayout.SOUTH);
    }

    /* ======================
       ログインパネル
     ====================== */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        loginIdField = new JTextField();
        loginPassField = new JPasswordField();

        panel.add(createLabeled("ユーザ名", loginIdField));
        panel.add(createLabeled("パスワード", loginPassField));

        JButton loginButton = new JButton("ログイン");
        loginButton.addActionListener(e ->
                onLoginButtonPressed(
                        loginIdField.getText(),
                        new String(loginPassField.getPassword()))
        );

        panel.add(loginButton);
        return panel;
    }

    /* ======================
       新規登録パネル
     ====================== */
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        signupIdField = new JTextField();
        signupPassField = new JPasswordField();

        panel.add(createLabeled("ユーザ名", signupIdField));
        panel.add(createLabeled("パスワード", signupPassField));

        JButton signupButton = new JButton("登録する！");
        signupButton.addActionListener(e ->
                onSignUpButtonPressed(
                        signupIdField.getText(),
                        new String(signupPassField.getPassword()))
        );

        panel.add(signupButton);
        return panel;
    }

    private JPanel createLabeled(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }


    /* ======================
       画面表示
     ====================== */

    public void showLoginScreen() {
        switchScreen(this);
    }


    /* ======================
       ボタン処理
     ====================== */

    public void onLoginButtonPressed(String id, String pass) {
        if (!validateInput(id) || !validateInput(pass)) {
            showMessage("ID または パスワードが不正です");
            return;
        }
        controller.sendLoginRequest(id, pass);
    }

    public void onSignUpButtonPressed(String id, String pass) {
        if (!validateInput(id) || !validateInput(pass)) {
            showMessage("ID または パスワードが不正です");
            return;
        }
        controller.sendSignUpRequest(id, pass);
    }

    private boolean validateInput(String input) {
        return input != null && input.length() >= 3;
    }

    public void showMessage(String msg) {
        messageLabel.setText(msg);
    }
}

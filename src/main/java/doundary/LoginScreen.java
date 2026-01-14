package doundary;

import control.ClientController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginScreen extends Screen {

    private JTextField loginIdField;
    private JPasswordField loginPassField;

    private JTextField signupIdField;
    private JPasswordField signupPassField;

    private JLabel messageLabel;

    // タブ切替用
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JButton loginTabButton;
    private JButton signupTabButton;

    public LoginScreen(ClientController controller) {
        super(controller);
        initUI();
    }

    private void initUI() {
        // 1) 全体の背景・レイアウト
        setLayout(new BorderLayout());
        setBackground(new Color(255, 244, 200)); // バナナ系の淡い背景

        // 2) 上部タイトル
        JLabel title = new JLabel("チンチロリン", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(120, 70, 20)); // 茶系
        title.setBorder(new EmptyBorder(24, 16, 10, 16));
        add(title, BorderLayout.NORTH);

        // 3) 中央：カード風コンテナ（白背景＋枠）
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        add(centerWrap, BorderLayout.CENTER);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 70, 20), 2),
                new EmptyBorder(16, 16, 16, 16)
        ));
        card.setPreferredSize(new Dimension(420, 340));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        centerWrap.add(card, gbc);

        // 4) HTMLっぽいタブ（ボタン2つ） + CardLayout
        JPanel tabBar = new JPanel(new GridLayout(1, 2, 8, 0));
        tabBar.setOpaque(false);

        loginTabButton = createTabButton("ログイン", true);
        signupTabButton = createTabButton("新規登録", false);

        loginTabButton.addActionListener(e -> switchTab(true));
        signupTabButton.addActionListener(e -> switchTab(false));

        tabBar.add(loginTabButton);
        tabBar.add(signupTabButton);

        card.add(tabBar, BorderLayout.NORTH);

        // CardLayout 本体
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(16, 4, 4, 4));

        cardPanel.add(createLoginPanel(), "LOGIN");
        cardPanel.add(createSignupPanel(), "SIGNUP");
        card.add(cardPanel, BorderLayout.CENTER);

        // 5) 下部メッセージ領域（見た目を崩さない）
        messageLabel = new JLabel(" ", SwingConstants.CENTER); // 高さ固定
        messageLabel.setForeground(new Color(200, 50, 50));
        messageLabel.setBorder(new EmptyBorder(10, 16, 16, 16));
        add(messageLabel, BorderLayout.SOUTH);
    }

    // タブボタンの見た目（アクティブ/非アクティブ）
    private JButton createTabButton(String text, boolean active) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (active) {
            b.setBackground(new Color(255, 204, 0));
            b.setForeground(new Color(70, 40, 10));
        } else {
            b.setBackground(new Color(245, 245, 245));
            b.setForeground(new Color(120, 70, 20));
        }
        b.setBorder(BorderFactory.createLineBorder(new Color(120, 70, 20), 2));
        b.setOpaque(true);
        return b;
    }

    private void switchTab(boolean login) {
        if (login) {
            cardLayout.show(cardPanel, "LOGIN");
            setTabActive(loginTabButton, true);
            setTabActive(signupTabButton, false);
            showMessage(" ");
        } else {
            cardLayout.show(cardPanel, "SIGNUP");
            setTabActive(loginTabButton, false);
            setTabActive(signupTabButton, true);
            showMessage(" ");
        }
    }

    private void setTabActive(JButton b, boolean active) {
        if (active) {
            b.setBackground(new Color(255, 204, 0));
            b.setForeground(new Color(70, 40, 10));
        } else {
            b.setBackground(new Color(245, 245, 245));
            b.setForeground(new Color(120, 70, 20));
        }
    }

    /* ======================
       ログインパネル
     ====================== */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());

        loginIdField = new JTextField();
        loginPassField = new JPasswordField();

        styleField(loginIdField);
        styleField(loginPassField);

        JButton loginButton = createPrimaryButton("ログイン");
        loginButton.addActionListener(e ->
                onLoginButtonPressed(
                        loginIdField.getText(),
                        new String(loginPassField.getPassword()))
        );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(8, 8, 8, 8);

        panel.add(createLabeled("ユーザ名", loginIdField), c);

        c.gridy++;
        panel.add(createLabeled("パスワード", loginPassField), c);

        c.gridy++;
        panel.add(loginButton, c);

        return panel;
    }

    /* ======================
       新規登録パネル
     ====================== */
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());

        signupIdField = new JTextField();
        signupPassField = new JPasswordField();

        styleField(signupIdField);
        styleField(signupPassField);

        JButton signupButton = createPrimaryButton("登録する！");
        signupButton.addActionListener(e ->
                onSignUpButtonPressed(
                        signupIdField.getText(),
                        new String(signupPassField.getPassword()))
        );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets = new Insets(8, 8, 8, 8);

        panel.add(createLabeled("ユーザ名", signupIdField), c);

        c.gridy++;
        panel.add(createLabeled("パスワード", signupPassField), c);

        c.gridy++;
        panel.add(signupButton, c);

        return panel;
    }

    private JPanel createLabeled(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(120, 70, 20));

        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void styleField(JComponent field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 34));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 70, 20), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        field.setBackground(Color.WHITE);
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBackground(new Color(255, 204, 0));
        b.setForeground(new Color(70, 40, 10));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 70, 20), 2),
                new EmptyBorder(10, 12, 10, 12)
        ));
        b.setOpaque(true);
        return b;
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
        // 高さ固定のため空文字は " " にする
        if (msg == null || msg.isBlank()) {
            messageLabel.setText(" ");
        } else {
            messageLabel.setText(msg);
        }
    }
}

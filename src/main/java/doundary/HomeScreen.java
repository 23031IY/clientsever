package doundary;

import control.ClientController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomeScreen extends Screen {

    private JLabel userLabel;
    private JLabel statusLabel;
    private JLabel messageLabel;

    private JButton matchButton;
    private JButton logoutButton;

    public HomeScreen(ClientController controller) {
        super(controller);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 244, 200)); // LoginScreen と合わせる

        // ===== 上部：タイトル + ユーザーパネル =====
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(18, 16, 8, 16));

        JLabel title = new JLabel("チンチロリン", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(new Color(120, 70, 20));
        top.add(title, BorderLayout.CENTER);

        // 右上：ユーザーパネル（後で setUsername で反映）
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        userPanel.setOpaque(false);

        userLabel = new JLabel("User: ---");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        userLabel.setForeground(new Color(120, 70, 20));

        logoutButton = createSecondaryButton("ログアウト");
        logoutButton.addActionListener(e -> onLogoutPressed());

        userPanel.add(userLabel);
        userPanel.add(logoutButton);

        top.add(userPanel, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // ===== 中央：カード（白背景＋枠） =====
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        add(centerWrap, BorderLayout.CENTER);

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 70, 20), 2),
                new EmptyBorder(18, 18, 18, 18)
        ));
        card.setPreferredSize(new Dimension(420, 260));

        // 中央寄せで配置
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        centerWrap.add(card, gbc);

        // メインボタン
        matchButton = createPrimaryButton("群れに合流する");
        matchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        matchButton.addActionListener(e -> onMatchButtonPressed());

        // ステータス
        statusLabel = new JLabel("待機中", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(new Color(120, 70, 20));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // 説明文（必要なら）
        JLabel hint = new JLabel("マッチングが成立すると対戦画面に移動します", SwingConstants.CENTER);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hint.setForeground(new Color(140, 90, 40));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        hint.setBorder(new EmptyBorder(10, 0, 0, 0));

        card.add(Box.createVerticalGlue());
        card.add(matchButton);
        card.add(statusLabel);
        card.add(hint);
        card.add(Box.createVerticalGlue());

        // ===== 下部：メッセージ =====
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(new Color(200, 50, 50));
        messageLabel.setBorder(new EmptyBorder(10, 16, 16, 16));
        add(messageLabel, BorderLayout.SOUTH);
    }

    // ===== 外部からの反映（Login 成功後に Controller が呼ぶ想定） =====
    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            userLabel.setText("User: ---");
        } else {
            userLabel.setText("User: " + username);
        }
    }

    public void setStatus(String status) {
        if (status == null || status.isBlank()) {
            statusLabel.setText(" ");
        } else {
            statusLabel.setText(status);
        }
    }

    // ===== 画面表示 =====
    public void showHomeScreen() {
        switchScreen(this);
    }

    // ===== ボタン処理 =====
    private void onMatchButtonPressed() {
        // UI側の即時フィードバック
        setStatus("マッチング中...");
        showMessage(" ");
        controller.notifyStartMatching();
    }

    private void onLogoutPressed() {
        controller.executeLogout();
    }

    public void showMessage(String msg) {
        if (msg == null || msg.isBlank()) {
            messageLabel.setText(" ");
        } else {
            messageLabel.setText(msg);
        }
    }

    // ===== ボタン部品 =====
    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBackground(new Color(255, 204, 0));
        b.setForeground(new Color(70, 40, 10));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 70, 20), 2),
                new EmptyBorder(10, 14, 10, 14)
        ));
        b.setOpaque(true);
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBackground(new Color(245, 245, 245));
        b.setForeground(new Color(120, 70, 20));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 70, 20), 2),
                new EmptyBorder(6, 10, 6, 10)
        ));
        b.setOpaque(true);
        return b;
    }
}

package doundary;

import communication.message.AppMessage;
import control.ClientController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResultScreen extends Screen {

    private JLabel messageLabel;
    private List<RankRow> rows;

    public ResultScreen(ClientController controller) {
        super(controller);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 244, 200)); // Login/Homeと合わせる

        // ===== 上部タイトル =====
        JLabel title = new JLabel("最終順位発表", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(new Color(120, 70, 20));
        title.setBorder(new EmptyBorder(22, 16, 10, 16));
        add(title, BorderLayout.NORTH);

        // ===== 中央カード =====
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        add(centerWrap, BorderLayout.CENTER);

        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 70, 20), 2),
                new EmptyBorder(16, 16, 16, 16)
        ));
        card.setPreferredSize(new Dimension(460, 360));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        centerWrap.add(card, gbc);

        // ===== ランキング行（最大4枠） =====
        rows = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            RankRow row = new RankRow(i);
            row.setAlignmentX(Component.CENTER_ALIGNMENT);
            rows.add(row);
            card.add(row);
            card.add(Box.createVerticalStrut(10));
        }

        // ===== 戻るボタン =====
        JButton back = createPrimaryButton("ホームへ戻る");
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.addActionListener(e -> onReturnHomeButtonPressed());

        card.add(Box.createVerticalStrut(8));
        card.add(back);

        // ===== 下部メッセージ =====
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(new Color(200, 50, 50));
        messageLabel.setBorder(new EmptyBorder(10, 16, 16, 16));
        add(messageLabel, BorderLayout.SOUTH);
    }

    /**
     * AppMessageから順位を計算し、画面に反映
     */
    public void showResult(AppMessage msg) {
        // null安全
        if (msg == null || msg.players == null) {
            clearRanking();
            showMessage("結果データがありません");
            return;
        }

        // ownedBananas 降順でソート
        List<AppMessage.PlayerState> sorted = msg.players.stream()
                .sorted(Comparator.comparingInt(
                        (AppMessage.PlayerState p) -> p.ownedBananas
                ).reversed())
                .toList();

        // 最大4枠に反映
        for (int i = 0; i < rows.size(); i++) {
            if (i < sorted.size()) {
                AppMessage.PlayerState p = sorted.get(i);
                rows.get(i).setData(p.name, p.ownedBananas);
            } else {
                rows.get(i).setEmpty();
            }
        }

        showMessage(" ");
        revalidate();
        repaint();
    }

    private void clearRanking() {
        for (RankRow r : rows) {
            r.setEmpty();
        }
        revalidate();
        repaint();
    }

    public void onReturnHomeButtonPressed() {
        controller.transitionToHomeScreen();
    }

    public void showResultScreen() {
        switchScreen(this);
    }

    public void showMessage(String msg) {
        if (msg == null || msg.isBlank()) {
            messageLabel.setText(" ");
        } else {
            messageLabel.setText(msg);
        }
    }

    // ===== ボタン（Home/Login と揃える） =====
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

    /**
     * ランキング1行分のUI部品
     */
    private static class RankRow extends JPanel {
        private final JLabel rankLabel;
        private final JLabel nameLabel;
        private final JLabel bananaLabel;

        RankRow(int rank) {
            setLayout(new BorderLayout(10, 0));
            setOpaque(true);
            setBackground(new Color(255, 252, 235)); // 少しだけ色付き
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(120, 70, 20), 1),
                    new EmptyBorder(10, 12, 10, 12)
            ));
            setMaximumSize(new Dimension(440, 54));

            rankLabel = new JLabel(rank + "位");
            rankLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            rankLabel.setForeground(new Color(120, 70, 20));

            nameLabel = new JLabel("---");
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            nameLabel.setForeground(new Color(70, 40, 10));

            bananaLabel = new JLabel("--- bananas", SwingConstants.RIGHT);
            bananaLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            bananaLabel.setForeground(new Color(120, 70, 20));

            add(rankLabel, BorderLayout.WEST);
            add(nameLabel, BorderLayout.CENTER);
            add(bananaLabel, BorderLayout.EAST);
        }

        void setData(String name, int bananas) {
            nameLabel.setText(name != null && !name.isBlank() ? name : "---");
            bananaLabel.setText(bananas + " bananas");
        }

        void setEmpty() {
            nameLabel.setText("---");
            bananaLabel.setText("--- bananas");
        }
    }
}



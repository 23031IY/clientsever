package doundary;

import communication.message.AppMessage;
import control.ClientController;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;

public class BattleScreen extends Screen {

    // ===== 上部 =====
    private JLabel titleLabel;
    private JLabel timerLabel;
    private JLabel phaseLabel;

    // ===== 役表示（成立役カットイン） =====
    private JPanel handPanel;
    private JLabel handImageLabel;
    private JLabel handTextLabel;
    private Timer handHideTimer;

    // ===== 中央：プレイヤー一覧 =====
    private JPanel playersGrid;
    private final Map<Integer, PlayerCard> playerCardsById = new HashMap<>();

    // ===== 下部：BET / ROLL =====
    private JTextField betField;
    private JButton betButton;
    private JButton rollButton;

    // ===== 下部：メッセージ =====
    private JLabel messageLabel;

    // 任意：自分のプレイヤーID（分かるならハイライト可能）
    private Integer myPlayerId = null;

    public BattleScreen(ClientController controller) {
        super(controller);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 244, 200)); // バナナ系背景

        // ===== 上部（タイトル＋状態＋タイマー） =====
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(18, 16, 10, 16));

        titleLabel = new JLabel("バトル", SwingConstants.LEFT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(new Color(120, 70, 20));

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);

        phaseLabel = new JLabel("待機中");
        phaseLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        phaseLabel.setForeground(new Color(120, 70, 20));

        timerLabel = new JLabel("残り: --");
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        timerLabel.setForeground(new Color(120, 70, 20));

        topRight.add(phaseLabel);
        topRight.add(timerLabel);

        top.add(titleLabel, BorderLayout.WEST);
        top.add(topRight, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // ===== 中央：カード =====
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        add(centerWrap, BorderLayout.CENTER);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 70, 20), 2),
                new EmptyBorder(14, 14, 14, 14)
        ));
        card.setPreferredSize(new Dimension(600, 440));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        centerWrap.add(card, gbc);

        // ===== card 上部（タイトル＋役表示） =====
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel playersTitle = new JLabel("プレイヤー状況");
        playersTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        playersTitle.setForeground(new Color(120, 70, 20));
        playersTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        playersTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(playersTitle);

        // 役表示パネル（通常は非表示）
        handPanel = new JPanel(new BorderLayout(10, 0));
        handPanel.setOpaque(true);
        handPanel.setBackground(new Color(255, 252, 235));
        handPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(120, 70, 20), 2),
                new EmptyBorder(10, 12, 10, 12)
        ));
        handPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        handImageLabel = new JLabel();
        handImageLabel.setPreferredSize(new Dimension(160, 110));

        handTextLabel = new JLabel(" ");
        handTextLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        handTextLabel.setForeground(new Color(70, 40, 10));

        handPanel.add(handImageLabel, BorderLayout.WEST);
        handPanel.add(handTextLabel, BorderLayout.CENTER);
        handPanel.setVisible(false);

        header.add(handPanel);

        card.add(header, BorderLayout.NORTH);

        // ===== プレイヤー一覧（スクロール可） =====
        playersGrid = new JPanel(new GridLayout(0, 2, 10, 10)); // 2列で可変
        playersGrid.setOpaque(false);

        JScrollPane scroll = new JScrollPane(playersGrid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        card.add(scroll, BorderLayout.CENTER);

        // ===== 下部：操作＋メッセージ =====
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 16, 16, 16));

        JPanel actions = new JPanel(new GridBagLayout());
        actions.setOpaque(false);

        betField = new JTextField();
        styleField(betField);

        betButton = createPrimaryButton("BET");
        betButton.addActionListener(e -> onBetPressed());

        rollButton = createPrimaryButton("ROLL");
        rollButton.addActionListener(e -> onRollDiceButtonPressed());

        // 初期はBET不可（showBetUI() で有効化）
        betField.setEnabled(false);
        betButton.setEnabled(false);
        rollButton.setEnabled(true);

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 10);
        actions.add(createLabeled("賭けバナナ数", betField), c);

        c.gridx = 1;
        c.weightx = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(16, 0, 0, 10);
        actions.add(betButton, c);

        c.gridx = 2;
        c.insets = new Insets(16, 0, 0, 0);
        actions.add(rollButton, c);

        bottom.add(actions, BorderLayout.NORTH);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(new Color(200, 50, 50));
        messageLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottom.add(messageLabel, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);
    }

    /* ======================
       画面表示
     ====================== */

    public void showBattleScreen() {
        SwingUtilities.invokeLater(() -> {
            phaseLabel.setText("進行中");
            showMessage(" ");
            switchScreen(this);
        });
    }

    public void showBetUI() {
        SwingUtilities.invokeLater(() -> {
            phaseLabel.setText("BET フェーズ");

            betField.setEnabled(true);
            betButton.setEnabled(true);

            // BET中はROLL無効
            rollButton.setEnabled(false);

            showMessage("賭けバナナ数を入力して BET を押してください");
        });
    }


    public void showRollUI() {
        SwingUtilities.invokeLater(() -> {
            phaseLabel.setText("ROLL フェーズ");

            // ROLL を有効化
            rollButton.setEnabled(true);

            // BET は無効化（フェーズが違うため）
            betField.setEnabled(false);
            betButton.setEnabled(false);

            // betField.setText("");

            showMessage("ROLL を押してください");
        });
    }

    /* ======================
       操作（既存メソッド維持）
     ====================== */

    public void onRollDiceButtonPressed() {
        SwingUtilities.invokeLater(() -> showMessage("サイコロを振っています..."));
        controller.sendBattleInfo(); // 既存仕様
    }

    public void bet(int betBanana) {
        controller.sendBattleInfo(betBanana); // 既存仕様
    }

    private void onBetPressed() {
        String raw = betField.getText();
        int bet;
        try {
            bet = Integer.parseInt(raw.trim());
        } catch (Exception ex) {
            showMessage("BET は整数で入力してください");
            return;
        }
        if (bet < 0) {
            showMessage("BET は 0 以上で入力してください");
            return;
        }

        showMessage("BET を送信しました: " + bet);
        bet(bet);

        // 送信後の入力制御（仕様次第）
        betField.setEnabled(false);
        betButton.setEnabled(false);
    }

    /* ======================
       動的更新（Controller から呼ぶ）
     ====================== */

    public void showRemainingTime(int timer) {
        SwingUtilities.invokeLater(() -> timerLabel.setText("残り: " + timer));
    }

    public void updateScreen(List<AppMessage.PlayerState> playerStates) {
        SwingUtilities.invokeLater(() -> {
            if (playerStates == null) return;

            Set<Integer> currentIds = new HashSet<>();
            for (AppMessage.PlayerState ps : playerStates) {
                if (ps == null) continue;

                currentIds.add(ps.playerId);
                PlayerCard card = playerCardsById.get(ps.playerId);
                if (card == null) {
                    card = new PlayerCard();
                    playerCardsById.put(ps.playerId, card);
                    playersGrid.add(card);
                }
                card.update(ps, Objects.equals(myPlayerId, ps.playerId));
            }

            // いなくなったプレイヤーがあれば削除（仕様により不要なら消してOK）
            Iterator<Map.Entry<Integer, PlayerCard>> it = playerCardsById.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, PlayerCard> e = it.next();
                if (!currentIds.contains(e.getKey())) {
                    playersGrid.remove(e.getValue());
                    it.remove();
                }
            }

            playersGrid.revalidate();
            playersGrid.repaint();
        });
    }

    /**
     * HAND通知用：AppMessage(handName, playerId, players) を受け取って
     * 「成立プレイヤー名 + 役名 + 役画像」を表示する
     */
    public void showHand(AppMessage msg) {
        SwingUtilities.invokeLater(() -> {
            if (msg == null || msg.players == null) {
                hideHandPanel();
                return;
            }
            if (msg.handName == null || msg.handName.trim().isEmpty()) {
                hideHandPanel();
                return;
            }

            // playerId から成立プレイヤーを特定
            AppMessage.PlayerState winner = null;
            for (AppMessage.PlayerState ps : msg.players) {
                if (ps != null && ps.playerId == msg.playerId) {
                    winner = ps;
                    break;
                }
            }

            String playerName;
            if (winner != null && winner.name != null && !winner.name.trim().isEmpty()) {
                playerName = winner.name;
            } else {
                playerName = "Player#" + msg.playerId;
            }

            String handName = msg.handName;

            // テキスト
            handTextLabel.setText(playerName + " の役: " + handName);

            // 役名 → JPG
            String file = yakuToImageFile(handName);

            // 画像読み込み
            ImageIcon icon = loadYakuIcon(file, 160, 110);
            handImageLabel.setIcon(icon);

            // 表示
            handPanel.setVisible(true);
            handPanel.revalidate();
            handPanel.repaint();

            // 自動非表示（任意）
            if (handHideTimer != null && handHideTimer.isRunning()) {
                handHideTimer.stop();
            }
            handHideTimer = new Timer(2500, e -> hideHandPanel());
            handHideTimer.setRepeats(false);
            handHideTimer.start();
        });
    }

    private void hideHandPanel() {
        if (handHideTimer != null && handHideTimer.isRunning()) {
            handHideTimer.stop();
        }
        if (handPanel != null) handPanel.setVisible(false);
        if (handTextLabel != null) handTextLabel.setText(" ");
        if (handImageLabel != null) handImageLabel.setIcon(null);
    }

    public void setMyPlayerId(Integer myPlayerId) {
        this.myPlayerId = myPlayerId;
    }

    /* ======================
       役名 → 画像ファイル名
     ====================== */

    private String yakuToImageFile(String handName) {
        // 文字列ブレに強い contains ベース
        if (handName.contains("フクロ") && handName.contains("テナガ")) return "fukurotenaga.jpg";
        if (handName.contains("テナガ")) return "tenaga.jpg";
        if (handName.contains("クロ")) return "kuro.jpg";
        if (handName.contains("ナガ")) return "naga.jpg";

        // 役なし等のフォールバック（必要なら「yaku_nashi.jpg」等に変更）
        return "naga.jpg";
    }

    /* ======================
       画像読み込み（classpath優先 → 実ファイル fallback）
     ====================== */

    private ImageIcon loadYakuIcon(String fileName, int w, int h) {
        if (fileName == null) return null;

        // (A) classpath: /UserInterface/xxx.jpg
        URL url = getClass().getResource("/UserInterface/" + fileName);
        if (url != null) {
            return scale(new ImageIcon(url), w, h);
        }

        // (B) 実ファイル fallback（開発時）
        File f1 = new File("src/UserInterface/" + fileName);
        if (f1.exists()) return scale(new ImageIcon(f1.getAbsolutePath()), w, h);

        File f2 = new File("UserInterface/" + fileName);
        if (f2.exists()) return scale(new ImageIcon(f2.getAbsolutePath()), w, h);

        return null;
    }

    private ImageIcon scale(ImageIcon icon, int w, int h) {
        if (icon == null || icon.getIconWidth() <= 0) return icon;
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    /* ======================
       共通UI部品
     ====================== */

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
                new EmptyBorder(10, 14, 10, 14)
        ));
        b.setOpaque(true);
        return b;
    }

    public void showMessage(String msg) {
        SwingUtilities.invokeLater(() -> {
            if (msg == null || msg.trim().isEmpty()) {
                messageLabel.setText(" ");
            } else {
                messageLabel.setText(msg);
            }
        });
    }

    /* ======================
       内部クラス：プレイヤーカード
     ====================== */

    private static class PlayerCard extends JPanel {
        private final JLabel nameLabel;
        private final JLabel bananaLabel;
        private final JLabel betLabel;
        private final JLabel dealerLabel;

        PlayerCard() {
            setLayout(new BorderLayout(8, 0));
            setOpaque(true);
            setBackground(new Color(255, 252, 235));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(120, 70, 20), 1),
                    new EmptyBorder(10, 12, 10, 12)
            ));
            setPreferredSize(new Dimension(260, 86));

            JPanel left = new JPanel();
            left.setOpaque(false);
            left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

            nameLabel = new JLabel("---");
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            nameLabel.setForeground(new Color(70, 40, 10));

            bananaLabel = new JLabel("Bananas: ---");
            bananaLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            bananaLabel.setForeground(new Color(120, 70, 20));

            betLabel = new JLabel("Bet: ---");
            betLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            betLabel.setForeground(new Color(120, 70, 20));

            left.add(nameLabel);
            left.add(Box.createVerticalStrut(4));
            left.add(bananaLabel);
            left.add(betLabel);

            dealerLabel = new JLabel(" ");
            dealerLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            dealerLabel.setForeground(new Color(120, 70, 20));
            dealerLabel.setHorizontalAlignment(SwingConstants.RIGHT);

            add(left, BorderLayout.CENTER);
            add(dealerLabel, BorderLayout.EAST);
        }

        void update(AppMessage.PlayerState ps, boolean isMe) {
            String n = (ps.name != null && !ps.name.trim().isEmpty())
                    ? ps.name
                    : ("Player#" + ps.playerId);

            nameLabel.setText(n);
            bananaLabel.setText("Bananas: " + ps.ownedBananas);
            betLabel.setText("Bet: " + ps.currentBetBananas);
            dealerLabel.setText(ps.dealer ? "親" : " ");

            if (isMe) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(70, 40, 10), 2),
                        new EmptyBorder(10, 12, 10, 12)
                ));
            } else {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(120, 70, 20), 1),
                        new EmptyBorder(10, 12, 10, 12)
                ));
            }
        }
    }
}

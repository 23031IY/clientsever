package doundary;

import control.ClientController;
import javax.swing.*;
import java.awt.*;

public class HomeScreen extends Screen {

    private JPanel panel;

    public HomeScreen(ClientController controller) {
        super(controller);
        initUI();
    }

    private void initUI() {
        panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("チンチロリン", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        panel.add(title, BorderLayout.NORTH);

        JButton matchButton = new JButton("群れに合流する");
        matchButton.addActionListener(e -> onMatchButtonPressed());

        JButton logoutButton = new JButton("森に帰る");
        logoutButton.addActionListener(e -> onLogoutButtonPressed());

        JPanel center = new JPanel();
        center.add(matchButton);
        center.add(logoutButton);

        panel.add(center, BorderLayout.CENTER);
    }

    public void onMatchButtonPressed() {
        controller.notifyStartMatching();
        showMessage("マッチング中...");
    }

    public void onLogoutButtonPressed() {
        controller.executeLogout();
    }

    public void showHomeScreen() {
        switchScreen(panel);
    }
}

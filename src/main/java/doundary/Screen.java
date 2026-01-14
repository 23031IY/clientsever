package doundary;

import control.ClientController;

import javax.swing.*;

public abstract class Screen extends JPanel {

    protected ClientController controller;
    protected static JFrame frame; // 共有フレーム

    public Screen(ClientController controller) {
        this.controller = controller;
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    protected void switchScreen(JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }
}


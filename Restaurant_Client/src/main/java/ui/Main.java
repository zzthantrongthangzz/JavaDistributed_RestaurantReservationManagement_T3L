package ui;
import java.awt.EventQueue;
public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                DangNhap_UI LoginFrame = new DangNhap_UI();
                LoginFrame.setVisible(true);
                LoginFrame.setLocationRelativeTo(null);
                LoginFrame.setResizable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
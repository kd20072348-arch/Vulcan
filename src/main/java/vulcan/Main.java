package vulcan;

import vulcan.database.DatabaseManager;
import vulcan.view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        // All Swing work must happen on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> new MainFrame(new DatabaseManager()).setVisible(true));
    }
}

package com.gabriel.slcas;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.gabriel.slcas.gui.MainWindow;
import com.gabriel.slcas.model.LibraryDatabase;
import com.gabriel.slcas.utils.FileHandler;
import com.gabriel.slcas.utils.SeedData;

/**
 * Starts the application by loading any saved data, or creating some sample data if no save file exists.
 */
public class App {

    private static final String AUTO_SAVE_PATH = "slcas_autosave.json";

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // fall back to the platform default look and feel
        }

        LibraryDatabase db = new LibraryDatabase();

        if (FileHandler.exists(AUTO_SAVE_PATH)) {
            try {
                FileHandler.load(db, AUTO_SAVE_PATH);
            } catch (Exception e) {
                SeedData.populate(db);
            }
        } else {
            SeedData.populate(db);
        }

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow(db);
            window.setVisible(true);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    FileHandler.save(db, AUTO_SAVE_PATH);
                } catch (Exception ignored) { }
            }));
        });
    }
}

import Controller.SwingController;
import Model.HAPIClient;
import Model.FHIRMonitorManager;
import Model.MonitorManager;
import View.*;

import javax.swing.*;

/**
 * Begins the entire application.
 */
public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // creating objects for MVC architecture
            View view = new MainWindow();
            FHIRMonitorManager manager = new MonitorManager(new HAPIClient());
            new SwingController(view, manager);

            view.setMonitor(manager);
            view.display();
        });
    }
}

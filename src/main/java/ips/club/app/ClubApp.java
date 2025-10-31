package ips.club.app;

import ips.club.controller.IncidentsController;
import ips.club.ui.IncidentWindow;
import ips.util.Database;

import javax.swing.*;

public class ClubApp {

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignore) {}

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	runIncidentWindow();
            }
        });
        
        
    }
    
    public static void runIncidentWindow() {
    	try {
            Database db = new Database();
            db.createDatabase(true);
            IncidentsController controller = new IncidentsController();

            IncidentWindow window = new IncidentWindow(controller);
            window.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error iniciando la aplicación:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }	
    }
}

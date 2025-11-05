package ips.club.app;

import ips.club.controller.IncidentsController;
import ips.club.controller.LocationsController;
import ips.club.controller.ReceiptBatchController;
import ips.club.controller.ReceiptsController;
import ips.club.controller.UsersController;
import ips.club.model.User;
import ips.club.ui.LoginWindow;
import ips.club.ui.MenuWindow;
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
        } catch (Exception ignore) {
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                Database db = new Database();
                db.createDatabase(false);
                db.loadDatabase();

                IncidentsController incController = new IncidentsController();
                UsersController usersController = new UsersController();
                LocationsController locationsController = new LocationsController();
                ReceiptsController receiptsController = new ReceiptsController();
                ReceiptBatchController receiptBatchController = new ReceiptBatchController();

                LoginWindow login = new LoginWindow(usersController);
                login.setVisible(true);
                User selected = login.getSelectedUser();
                if (selected == null) {
                    System.out.println("No se seleccionó usuario. Saliendo...");
                    return;
                }
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MenuWindow menu = new MenuWindow(selected, incController, locationsController, receiptsController, receiptBatchController);
                        menu.setVisible(true);
                    }
                });
            }
        });
    }
}

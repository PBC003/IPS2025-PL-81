package ips.club.ui;

import ips.club.controller.IncidentsController;
import ips.club.controller.LocationsController;
import ips.club.controller.ReceiptBatchController;
import ips.club.controller.ReceiptsController;
import ips.club.controller.UsersController;
import ips.club.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class MenuWindow extends JFrame {

    private final User currentUser;
    private final IncidentsController incidentsController;
    private final LocationsController locationsController;
    private final ReceiptsController receiptsController;
    private final ReceiptBatchController receiptBatchController;
    private final UsersController usersController;

    private JLabel lblUser;
    private JButton btnIncidents;
    private JButton btnReceipts;
    private JButton btnExit;
    private JButton btnBatch;

    public MenuWindow(User currentUser,IncidentsController incidentsController, LocationsController locationsController, ReceiptsController receiptsController, ReceiptBatchController receiptBatchController, UsersController usersController) {
        super("Menú principal");
        this.currentUser = currentUser;
        this.incidentsController = incidentsController;
        this.locationsController = locationsController;
        this.receiptsController = receiptsController;
        this.usersController = usersController;
        this.receiptBatchController = receiptBatchController;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        pack();
        setMinimumSize(new Dimension(480, 320));
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(12,12,12,12));
        setContentPane(root);

        lblUser = new JLabel("Usuario: " + currentUser.getName() + " (" + currentUser.getEmail() + ")" );
        lblUser.setFont(lblUser.getFont().deriveFont(Font.BOLD));
        root.add(lblUser, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0,1,8,8));

        btnIncidents = new JButton("Gestión de incidencias");
        center.add(btnIncidents);

        btnReceipts = new JButton("Gestión de Recibos");
        center.add(btnReceipts);

        btnBatch= new JButton("Gestión de Lotes");
        center.add(btnBatch);

        root.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnExit = new JButton("Salir");
        south.add(btnExit);
        root.add(south, BorderLayout.SOUTH);

        btnIncidents.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                openIncidentsWindow();
            }
        });

        btnReceipts.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
            	openReceiptsWindow();
            }
        });
        btnBatch.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
            	openReceiptBatchWindow();
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void openIncidentsWindow() {
        IncidentWindow w = new IncidentWindow(incidentsController, locationsController, currentUser );
        w.setVisible(true);
    }

    private void openReceiptsWindow() {
    	ReceiptWindow w = new ReceiptWindow(receiptsController, usersController);
        w.setVisible(true);
    }
    private void openReceiptBatchWindow() {
    	ReceiptBatchWindow w = new ReceiptBatchWindow(receiptBatchController);
        w.setVisible(true);
    }
}

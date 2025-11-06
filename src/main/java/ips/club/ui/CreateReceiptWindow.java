package ips.club.ui;

import javax.swing.*;

import ips.club.controller.ReceiptsController;
import ips.club.controller.UsersController;
import ips.club.model.User;
import ips.util.ApplicationException;

import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class CreateReceiptWindow extends JDialog {
    private final ReceiptsController receiptController;
    private final UsersController userController;

    private JComboBox<User> cbUsers;
    private JTextField txtConcepto;
    private JButton btnCrear;
    private JButton btnCancelar;

    private boolean ok = false;

    public CreateReceiptWindow(ReceiptsController receiptController, UsersController userController) {
        super(null, "Crear Recibo", ModalityType.APPLICATION_MODAL);
        setLocationRelativeTo(null);
        this.receiptController = receiptController;
        this.userController = userController;

        initUI();
        pack();
    }

    private void initUI() {
        cbUsers = new JComboBox<>();
        loadUsers();

        txtConcepto = new JTextField(30);
        txtConcepto.setToolTipText("Deja vacío para usar el concepto por defecto (p.ej. 'Cuota Nov 2025').");

        btnCrear = new JButton("Crear");
        btnCancelar = new JButton("Cancelar");

        btnCrear.addActionListener(e -> onCrear());
        btnCancelar.addActionListener(e -> onCancelar());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Usuario:"), c);
        c.gridx = 1;
        c.gridy = 0;
        form.add(cbUsers, c);

        c.gridx = 0;
        c.gridy = 1;
        form.add(new JLabel("Concepto:"), c);
        c.gridx = 1;
        c.gridy = 1;
        form.add(txtConcepto, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnCancelar);
        buttons.add(btnCrear);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        List<User> users = userController.findEligibleUsersForThisMonth();
        DefaultComboBoxModel<User> model = new DefaultComboBoxModel<>();
        for (User u : users)
            model.addElement(u);
        cbUsers.setModel(model);

        if (users.isEmpty()) {
            cbUsers.setEnabled(false);
            btnCrear.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                    "Todos los usuarios ya tienen recibo en este mes.",
                    "Nada que crear",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            cbUsers.setSelectedIndex(0);
        }
    }

    private void onCrear() {
        User u = (User) cbUsers.getSelectedItem();
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un usuario.");
            return;
        }

        String concepto = txtConcepto.getText().trim();
        if (concepto.isEmpty())
            concepto = null;

        try {
            receiptController.createManualReceipt(u.getId().intValue(), u.getMonthlyFeeCents().intValue(), concepto);
            ok = true;
            dispose();
        } catch (ApplicationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inesperado al crear el recibo.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancelar() {
        ok = false;
        dispose();
    }

    public boolean isOk() {
        return ok;
    }
}

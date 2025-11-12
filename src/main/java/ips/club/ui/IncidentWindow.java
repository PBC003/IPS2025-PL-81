package ips.club.ui;

import ips.club.controller.IncidentsController;
import ips.club.controller.LocationsController;
import ips.club.controller.UsersController;
import ips.club.dto.IncidentDTO;
import ips.club.model.IncidentType;
import ips.club.model.Location;
import ips.club.model.User;
import ips.util.ApplicationException;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("serial")
public class IncidentWindow extends JFrame {

    private final IncidentsController incController;
    private final LocationsController locController;
    private final User currentUser;
    private final UsersController userController;

    private JLabel lblUser;
    private JComboBox<IncidentType> cbType;
    private JComboBox<Location> cbLocation;
    private JTextArea txtComment;

    private JButton btnCreate;
    private JButton btnClear;
    private JButton btnList;
    private JButton btnBack;

    private static final String TYPE_ACTIVA_LOCALIZACION = "Instalaciones";

    public IncidentWindow(IncidentsController incController, LocationsController locController, User currentUser,
            UsersController userController) {
        this.incController = Objects.requireNonNull(incController);
        this.locController = Objects.requireNonNull(locController);
        this.currentUser = Objects.requireNonNull(currentUser);
        this.userController = Objects.requireNonNull(userController);

        setTitle("Nueva incidencia");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 520);
        setLocationRelativeTo(null);

        initUI();
        loadTypes();
        loadLocations();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Crear incidencia");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        header.add(title, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;

        lblUser = new JLabel("Usuario: " + currentUser.getName() + " (id=" + currentUser.getId() + ")");
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = 2;
        form.add(lblUser, gbc);
        gbc.gridwidth = 1;

        cbType = new JComboBox<>();
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        form.add(cbType, gbc);
        y++;

        cbLocation = new JComboBox<>();
        cbLocation.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Localización:"), gbc);
        gbc.gridx = 1;
        form.add(cbLocation, gbc);
        y++;

        txtComment = new JTextArea(8, 20);
        txtComment.setLineWrap(true);
        txtComment.setWrapStyleWord(true);
        gbc.gridx = 0;
        gbc.gridy = y;
        form.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        form.add(new JScrollPane(txtComment), gbc);
        y++;

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnList = new JButton("Ver incidencias…");
        btnClear = new JButton("Limpiar");
        btnCreate = new JButton("Crear");
        btnBack = new JButton("Atrás");
        buttons.add(btnBack);
        buttons.add(btnList);
        buttons.add(btnClear);
        buttons.add(btnCreate);

        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        root.add(form, BorderLayout.CENTER);
        cbType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    IncidentType t = (IncidentType) e.getItem();
                    String display = t.toString();
                    boolean isInstalaciones = TYPE_ACTIVA_LOCALIZACION.equalsIgnoreCase(display);
                    cbLocation.setEnabled(isInstalaciones);
                    if (!isInstalaciones) {
                        cbLocation.setSelectedItem(null);
                    }
                }
            }
        });

        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreate();
            }
        });

        btnList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IncidentsListWindow listWindow = new IncidentsListWindow(incController, userController,
                        currentUser.getId());
                listWindow.setVisible(true);
            }
        });

        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

    }

    private void loadTypes() {
        try {
            cbType.removeAllItems();
            List<IncidentType> types = incController.loadIncidentTypes();
            for (IncidentType t : types)
                cbType.addItem(t);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar los tipos de Incidencias ", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLocations() {
        try {
            cbLocation.removeAllItems();
            List<Location> locs = locController.loadLocations();
            for (Location l : locs)
                cbLocation.addItem(l);
            cbLocation.setSelectedItem(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudieron cargar las localizaciones", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCreate() {
        try {

            IncidentType t = (IncidentType) cbType.getSelectedItem();
            if (t == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un tipo de incidencia.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String comment = txtComment.getText().trim();
            if (comment.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Location loc = (Location) cbLocation.getSelectedItem();
            boolean isInstalaciones = TYPE_ACTIVA_LOCALIZACION.equalsIgnoreCase(t.toString());
            if (!isInstalaciones)
                loc = null;
            if (isInstalaciones && (loc == null)) {
                JOptionPane.showMessageDialog(this, "La localizacion no puede estar vacía.", "Validación",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            IncidentDTO dto = new IncidentDTO(currentUser.getId(), t.getCode(), comment, loc);
            int newId = incController.createTicket(dto).getId();

            JOptionPane.showMessageDialog(this, ("Incidencia creada. ID = " + newId), "OK",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();

        } catch (ApplicationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        cbType.setSelectedIndex(cbType.getItemCount() > 0 ? 0 : -1);
        cbLocation.setSelectedItem(null);
        cbLocation.setEnabled(TYPE_ACTIVA_LOCALIZACION
                .equalsIgnoreCase(cbType.getSelectedItem() != null ? cbType.getSelectedItem().toString() : ""));
        txtComment.setText("");
    }
}

package ips.club.ui;

import ips.club.controller.IncidentsController;
import ips.club.dto.IncidentDTO;
import ips.club.model.IncidentType;
import ips.club.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;

@SuppressWarnings("serial")
public class IncidentWindow extends JFrame {

    private final IncidentsController incController;
    private final User currentUser;


    private JLabel lblChooseType;
    private JComboBox<IncidentType> cbTypes;
    private JLabel lblUser;
    private JLabel lblDynamic;
    private JTextArea txtComment;
    private JButton btnCreate;
    private JButton btnBack;

    public IncidentWindow(IncidentsController incController, User currentUser) {
        super("Crear incidencia");
        this.incController = incController;
        this.currentUser = currentUser;
        initUI();
        loadTypes();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(560, 360);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        lblUser = new JLabel("Usuario: " + currentUser.getName() +
                (currentUser.getEmail() != null ? " (" + currentUser.getEmail() + ")" : ""));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUser.setFont(lblUser.getFont().deriveFont(Font.BOLD));
        root.add(lblUser);
        root.add(Box.createVerticalStrut(8));
        
        lblChooseType = new JLabel("Seleccione el tipo de incidencia");
        lblChooseType.setFont(lblChooseType.getFont().deriveFont(Font.BOLD));
        JPanel p0 = new JPanel(new BorderLayout());
        p0.add(lblChooseType, BorderLayout.WEST);
        root.add(p0);
        root.add(Box.createVerticalStrut(8));

        cbTypes = new JComboBox<>();
        cbTypes.addItemListener(e -> {if (e.getStateChange() == ItemEvent.SELECTED) updateDynamicLabel();});
        JPanel p1 = new JPanel(new BorderLayout());
        p1.add(cbTypes, BorderLayout.CENTER);
        root.add(p1);
        root.add(Box.createVerticalStrut(8));
        
        lblDynamic = new JLabel("Descripción o localización");
        JPanel p2 = new JPanel(new BorderLayout());
        p2.add(lblDynamic, BorderLayout.WEST);
        root.add(p2);
        root.add(Box.createVerticalStrut(6));
        

        txtComment = new JTextArea(6, 40);
        txtComment.setLineWrap(true);
        txtComment.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(
                txtComment,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        JPanel p3 = new JPanel(new BorderLayout());
        p3.add(scroll, BorderLayout.CENTER);
        p3.setPreferredSize(new Dimension(520, 180));
        root.add(p3);
        root.add(Box.createVerticalStrut(10));

        btnBack = new JButton("Atrás");
        btnBack.addActionListener(e -> dispose());
        btnCreate = new JButton("Crear incidencia");
        btnCreate.addActionListener(e -> onCreateIncident());
        JPanel p4 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        p4.add(btnBack);
        p4.add(btnCreate);
        root.add(p4);
        
    }

    private void loadTypes() {
        List<IncidentType> types = incController.loadIncidentTypes();
        DefaultComboBoxModel<IncidentType> model = new DefaultComboBoxModel<IncidentType>();
        for (IncidentType t : types) model.addElement(t);
        cbTypes.setModel(model);
        if (model.getSize() > 0) cbTypes.setSelectedIndex(0);
        updateDynamicLabel();
        if (model.getSize() == 0) {
            JOptionPane.showMessageDialog(
                this,
                "No hay tipos de incidencia en la base de datos.",
                "Datos no cargados",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void updateDynamicLabel() {
        IncidentType t = (IncidentType) cbTypes.getSelectedItem();
        if (t == null) {
            lblDynamic.setText("Descripción o localización");
            return;
        }
        switch (t.getFieldType()) {
            case LOCATION:
                lblDynamic.setText("Localización");
                break;
            case DESCRIPTION:
                lblDynamic.setText("Descripción");
                break;
            default:
                lblDynamic.setText("Descripción o localización");
        }
    }

    private void onCreateIncident() {
        IncidentType t = (IncidentType) cbTypes.getSelectedItem();
        if (t == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un tipo de incidencia.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            IncidentDTO dto = new IncidentDTO(currentUser.getId(), t.getCode(), txtComment.getText());
            int newId = incController.createTicket(dto).getId();
            String msg = (newId > 0) ? "Incidencia creada. ID = " + newId : "Incidencia creada correctamente.";
            JOptionPane.showMessageDialog(this, msg, "OK", JOptionPane.INFORMATION_MESSAGE);
            txtComment.setText("");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validación", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

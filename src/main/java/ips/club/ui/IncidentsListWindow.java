package ips.club.ui;

import ips.club.controller.IncidentsController;
import ips.club.model.Incident;
import ips.club.model.IncidentType;
import ips.club.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class IncidentsListWindow extends JFrame {

    private final IncidentsController incController;
    private final User currentUser;

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> list;
    private JButton btnRefresh;
    private JButton btnBack;
    private JLabel lblCount;

    private Map<Integer, String> typeNamesByCode = new HashMap<Integer, String>();

    public IncidentsListWindow(IncidentsController incController, User currentUser) {
        this.incController = incController;
        this.currentUser = currentUser;
        initUI();
        loadTypes();
        loadData();
    }

    private void initUI() {
        setTitle("Incidencias - " + currentUser.getName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("Listado de incidencias");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        header.add(lblTitle, BorderLayout.WEST);
        lblCount = new JLabel(" ");
        header.add(lblCount, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(list);
        root.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnBack = new JButton("Atrás");
        btnRefresh = new JButton("Refrescar");
        actions.add(btnBack);
        actions.add(btnRefresh);
        root.add(actions, BorderLayout.SOUTH);

        btnBack.addActionListener(e -> dispose());
        btnRefresh.addActionListener(e -> loadData());
    }

    private void loadTypes() {
        try {
            List<IncidentType> types = incController.loadIncidentTypes();
            typeNamesByCode.clear();
            for (IncidentType t : types) {
                typeNamesByCode.put(t.getCode(), t.getName());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error cargando tipos de incidencia", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadData() {
        try {
            List<Incident> incidents = incController.loadIncident();
            listModel.clear();
            for (Incident i : incidents) {
                Integer id = i.getId();
                String status = String.valueOf(i.getStatus());
                String incName = typeNamesByCode.get(i.getIncCode());
                String created = i.getCreatedAt().toString();
                String desc = (i.getDescription().length()>60) ? i.getDescription().substring(0, 57) + "..." : i.getDescription();
                String line = String.format("[%d] (%s) %s | %s | %s",id, status,incName,created,desc);
                listModel.addElement(line);
            }
            lblCount.setText("Total: " + incidents.size());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error cargando incidencias", JOptionPane.ERROR_MESSAGE);
        }
    }
}

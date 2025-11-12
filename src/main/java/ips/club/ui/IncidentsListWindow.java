package ips.club.ui;

import ips.club.controller.IncidentsController;
import ips.club.controller.UsersController;
import ips.club.model.Incident;
import ips.club.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class IncidentsListWindow extends JFrame {

    private final IncidentsController incidentsController;
    private final UsersController userController;

    private final int currentUserId;
    private final boolean currentUserIsAdmin;

    private final DefaultListModel<Incident> listModel = new DefaultListModel<>();
    private JList<Incident> list;
    private JLabel lblCount;
    private JButton btnRefresh;
    private JButton btnClose;

    public IncidentsListWindow(IncidentsController incidentsController, UsersController userController, int currentUserId) {
        super("Listado de incidencias");
        this.incidentsController = incidentsController;
        this.userController = userController;
        this.currentUserId = currentUserId;
        this.currentUserIsAdmin = resolveIsAdmin(currentUserId);
        buildUI();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        loadData();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
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
        list.setCellRenderer(new IncidentRenderer());
        JScrollPane scroll = new JScrollPane(list);
        root.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRefresh = new JButton("Refrescar");
        btnClose = new JButton("Cerrar");
        footer.add(btnRefresh);
        footer.add(btnClose);
        root.add(footer, BorderLayout.SOUTH);

        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private boolean resolveIsAdmin(int userId) {
        try {
            User u = userController.findById(userId);
            if (u == null || u.getRole() == null)
                return false;
            return "ADMIN".equalsIgnoreCase(u.getRole());
        } catch (Exception e) {
            return false;
        }
    }

    private void loadData() {
        List<Incident> incidents;
        try {
            if (currentUserIsAdmin) {
                incidents = incidentsController.loadIncident();
            } else {
                incidents = incidentsController.findAllByReporter(currentUserId);
            }
        } catch (Exception e) {
            incidents = Collections.emptyList();
        }

        listModel.clear();
        for (Incident i : incidents)
            listModel.addElement(i);
        lblCount.setText("Total: " + listModel.size());
    }

    private static final class IncidentRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Incident) {
                Incident i = (Incident) value;
                String id = safe(i.getId());
                String status = safe(i.getStatus());
                String code = safe(i.getIncCode());
                String created = (i.getCreatedAt() != null) ? String.valueOf(i.getCreatedAt()) : "-";
                String desc = (i.getDescription() != null) ? i.getDescription() : "";
                if (desc.length() > 70)
                    desc = desc.substring(0, 67) + "...";
                String line = String.format("[%s] (%s) %s | %s | %s", id, status, code, created, desc);
                setText(line);
                setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            }
            return this;
        }

        private static String safe(Object o) {
            return (o == null) ? "-" : String.valueOf(o);
        }
    }
}

package ips.club.ui;

import ips.club.controller.ReservationController;
import ips.club.model.Location;
import ips.club.model.Reservation;
import ips.club.ui.table.ReservationTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ReservationListWindow extends JFrame {
    private final ReservationController controller;
    private final int userId;

    private JComboBox<ComboItem> cbLocation = new JComboBox<>();
    private JButton btnRefresh = new JButton("Refrescar");
    private JButton btnCreate = new JButton("Nueva reserva");
    private JButton btnCancel = new JButton("Salir");
    private JTable table = new JTable();
    private final ReservationTableModel model = new ReservationTableModel();

    public ReservationListWindow(ReservationController controller, int userId) {
        super("Reservas — Listado y filtro");
        this.controller = controller;
        this.userId = userId;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        cbLocation = new JComboBox<>();
        btnRefresh = new JButton("Refrescar");
        btnCreate = new JButton("Nueva reserva");
        btnCancel = new JButton("Salir");
        table = new JTable();

        table.setModel(model);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Instalación:"));
        top.add(cbLocation);
        top.add(btnRefresh);


        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        right.add(btnCancel);
        right.add(btnCreate);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(right, BorderLayout.SOUTH);

        loadLocations();
        loadTable();

        cbLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTable();
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTable();
            }
        });

        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateReservationWindow createW =
                    new CreateReservationWindow(controller,userId);
                createW.setVisible(true);
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadTable() {
        ComboItem item = (ComboItem) cbLocation.getSelectedItem();
        Integer locationId = (item != null) ? item.id : null;
        List<Reservation> list = controller.listReservations(locationId);
        model.setData(list);
    }

    private static class ComboItem {
        final Integer id;
        final String label;

        ComboItem(Integer id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private void loadLocations() {
        cbLocation.removeAllItems();
        cbLocation.addItem(new ComboItem(null, "Todas"));
        List<Location> locs = controller.findAllLocations();

        Map<Integer, String> names = new HashMap<>();
        for (Location l : locs) {
            cbLocation.addItem(new ComboItem(l.getId(), l.getName()));
            names.put(l.getId(), l.getName());
        }
        model.setLocationNames(names);

        cbLocation.setSelectedIndex(0);
    }
}

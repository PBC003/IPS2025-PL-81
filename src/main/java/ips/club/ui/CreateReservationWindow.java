package ips.club.ui;

import ips.club.controller.ReservationController;
import ips.club.model.Location;
import ips.club.model.Reservation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class CreateReservationWindow extends JDialog {
    private final ReservationController controller;
    private final int userId;

    private JComboBox<LocationItem> cbLocation = new JComboBox<>();
    private JSpinner spDay = new JSpinner();
    private JComboBox<Integer> cbMinutes = new JComboBox<>(new Integer[] { 60, 120 });
    private JComboBox<LocalTime> cbHour = new JComboBox<>();
    private JButton btnCreate = new JButton("Reservar");
    private JButton btnCancel = new JButton("Cancelar");
    private JLabel lblHint = new JLabel("Elige instalación, día y duración para ver horas libres");

    public CreateReservationWindow(ReservationController controller, int userId) {
        super(null, "Nueva reserva", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.userId = userId;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 280);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));
        initUI();
    }

    private void initUI() {
        cbLocation = new JComboBox<>();
        spDay = new JSpinner();
        cbMinutes = new JComboBox<>(new Integer[] { 60, 120 });
        cbHour = new JComboBox<>();
        btnCreate = new JButton("Reservar");
        btnCancel = new JButton("Cancelar");
        lblHint = new JLabel("Elige instalación, día y duración para ver horas libres");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;

        c.gridx = 0;
        form.add(new JLabel("Instalación:"), c);
        c.gridx = 1;
        form.add(cbLocation, c);

        c.gridy++;
        c.gridx = 0;
        form.add(new JLabel("Día:"), c);
        c.gridx = 1;
        form.add(spDay, c);

        c.gridy++;
        c.gridx = 0;
        form.add(new JLabel("Duración (min):"), c);
        c.gridx = 1;
        form.add(cbMinutes, c);

        c.gridy++;
        c.gridx = 0;
        form.add(new JLabel("Hora de inicio:"), c);
        c.gridx = 1;
        form.add(cbHour, c);

        JPanel south = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnCancel);
        buttons.add(btnCreate);
        south.add(lblHint, BorderLayout.WEST);
        south.add(buttons, BorderLayout.EAST);

        add(form, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        loadLocations();
        setupDaySpinner();
        cbMinutes.setSelectedItem(60);
        cbHour.setEnabled(false);
        btnCreate.setEnabled(false);

        cbLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAvailableHours();
            }
        });

        cbMinutes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAvailableHours();
            }
        });

        spDay.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                onDayChanged(e);
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCreate(e);
            }
        });

    }

    private void onDayChanged(ChangeEvent e) {
        updateAvailableHours();
    }

    private void loadLocations() {
        cbLocation.removeAllItems();
        List<Location> locs = controller.findAllLocations();
        for (Location l : locs) {
            cbLocation.addItem(new LocationItem(l.getId(), l.getName()));
        }
        if (cbLocation.getItemCount() > 0)
            cbLocation.setSelectedIndex(0);
    }

    private void setupDaySpinner() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now();
        LocalDate last = LocalDateTime.now().plusHours(72).toLocalDate();

        Date initial = Date.from(today.atStartOfDay(zone).toInstant());
        Date min = Date.from(today.atStartOfDay(zone).toInstant());
        Date max = Date.from(last.atTime(LocalTime.of(23, 59)).atZone(zone).toInstant());

        SpinnerDateModel model = new SpinnerDateModel(initial, min, max, Calendar.DAY_OF_MONTH);
        spDay.setModel(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spDay, "yyyy-MM-dd");
        spDay.setEditor(editor);
    }

    private void updateAvailableHours() {
        LocationItem li = (LocationItem) cbLocation.getSelectedItem();
        Integer minutes = (Integer) cbMinutes.getSelectedItem();
        if (li == null || minutes == null) {
            cbHour.removeAllItems();
            cbHour.setEnabled(false);
            btnCreate.setEnabled(false);
            return;
        }

        LocalDate day = getSelectedDay();
        List<LocalTime> hours = controller.computeAvailableStartHours(this.userId, li.id, day, minutes);

        cbHour.removeAllItems();
        for (LocalTime h : hours)
            cbHour.addItem(h);

        boolean has = cbHour.getItemCount() > 0;
        cbHour.setEnabled(has);
        btnCreate.setEnabled(has);
    }

    private LocalDate getSelectedDay() {
        Date d = (Date) spDay.getValue();
        return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void onCreate(ActionEvent e) {
        LocationItem li = (LocationItem) cbLocation.getSelectedItem();
        Integer minutes = (Integer) cbMinutes.getSelectedItem();
        LocalTime hour = (LocalTime) cbHour.getSelectedItem();

        if (li == null || minutes == null || hour == null) {
            JOptionPane.showMessageDialog(this, "Faltan datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate day = getSelectedDay();

        try {
            Reservation r = controller.createReservation(
                    this.userId, li.id, day, hour, minutes);
            JOptionPane.showMessageDialog(this,
                    "Reserva creada (ID " + r.getId() + ").",
                    "OK", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo crear", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class LocationItem {
        final int id;
        final String label;

        LocationItem(int id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

}

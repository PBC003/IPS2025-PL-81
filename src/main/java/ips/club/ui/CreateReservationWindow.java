package ips.club.ui;

import ips.club.controller.ReservationController;
import ips.club.model.Location;
import ips.club.model.Reservation;

import javax.swing.*;

import org.jdesktop.swingx.JXDatePicker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class CreateReservationWindow extends JDialog {
    private final ReservationController controller;
    private final int userId;

    private JComboBox<Location> cbLocation = new JComboBox<>();
    private JXDatePicker dpDay = new JXDatePicker();
    private JComboBox<LocalTime> cbHour = new JComboBox<>();
    private JButton btnCreate = new JButton("Reservar");
    private JButton btnCancel = new JButton("Cancelar");
    private JLabel lblHint = new JLabel("Elige instalación, día y duración para ver horas libres");

    private JToggleButton btn60 = new JToggleButton("60 minutos");
    private JToggleButton btn120 = new JToggleButton("120 minutos");

    private final ZoneId zone = ZoneId.systemDefault();
    private LocalDate minDate;
    private LocalDate maxDate;
    private LocalDate lastValidDay;

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
        dpDay = new JXDatePicker();
        cbHour = new JComboBox<>();
        btnCreate = new JButton("Reservar");
        btnCancel = new JButton("Cancelar");
        lblHint = new JLabel("Elige instalación, día y duración para ver horas libres");
        btn60 = new JToggleButton("60 minutos");
        btn120 = new JToggleButton("120 minutos");

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
        form.add(dpDay, c);

        c.gridy++;
        c.gridx = 0;
        form.add(new JLabel("Duración:"), c);
        c.gridx = 1;
        form.add(createMinutesPanel(), c);

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
        setupDayPicker();
        setupMinutesButtons();

        cbHour.setEnabled(false);
        btnCreate.setEnabled(false);

        cbLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAvailableHours();
            }
        });

        dpDay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onDayChanged();
            }
        });

        btn60.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAvailableHours();
            }
        });

        btn120.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAvailableHours();
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

    private JPanel createMinutesPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        ButtonGroup group = new ButtonGroup();
        group.add(btn60);
        group.add(btn120);
        panel.add(btn60);
        panel.add(btn120);
        return panel;
    }

    private void setupMinutesButtons() {
        btn60.setSelected(true);
    }

    private Integer getSelectedMinutes() {
        if (btn60.isSelected()) return 60;
        if (btn120.isSelected()) return 120;
        return null;
    }

    private void loadLocations() {
        cbLocation.removeAllItems();
        List<Location> locs = controller.findAllLocations();
        for (Location l : locs) {
            cbLocation.addItem(new Location(l.getId(), l.getName()));
        }
        if (cbLocation.getItemCount() > 0) {
            cbLocation.setSelectedIndex(0);
        }
    }

    private void setupDayPicker() {
        minDate = LocalDate.now();
        maxDate = LocalDateTime.now().plusHours(72).toLocalDate();
        lastValidDay = minDate;

        Date initial = Date.from(minDate.atStartOfDay(zone).toInstant());
        Date min = Date.from(minDate.atStartOfDay(zone).toInstant());
        Date max = Date.from(maxDate.atTime(LocalTime.of(23, 59)).atZone(zone).toInstant());

        dpDay.setDate(initial);
        dpDay.getMonthView().setLowerBound(min);
        dpDay.getMonthView().setUpperBound(max);
    }

    private void onDayChanged() {
        Date selected = dpDay.getDate();
        if (selected == null) {
            restoreLastValidDay();
            return;
        }
        LocalDate day = Instant.ofEpochMilli(selected.getTime()).atZone(zone).toLocalDate();
        if (day.isBefore(minDate) || day.isAfter(maxDate)) {
            JOptionPane.showMessageDialog(this, "La fecha seleccionada no es válida.", "Fecha no válida", JOptionPane.WARNING_MESSAGE);
            restoreLastValidDay();
            return;
        }
        lastValidDay = day;
        updateAvailableHours();
    }

    private void restoreLastValidDay() {
        if (lastValidDay != null) {
            Date d = Date.from(lastValidDay.atStartOfDay(zone).toInstant());
            dpDay.setDate(d);
        }
    }

    private LocalDate getSelectedDay() {
        Date d = dpDay.getDate();
        if (d == null && lastValidDay != null) {return lastValidDay;}
        if (d == null) {return null;}
        return Instant.ofEpochMilli(d.getTime()).atZone(zone).toLocalDate();
    }

    private void updateAvailableHours() {
        Location li = (Location) cbLocation.getSelectedItem();
        Integer minutes = getSelectedMinutes();
        LocalDate day = getSelectedDay();
        if (li == null || minutes == null || day == null) {
            cbHour.removeAllItems();
            cbHour.setEnabled(false);
            btnCreate.setEnabled(false);
            return;
        }

        List<LocalTime> hours = controller.computeAvailableStartHours(this.userId, li.getId(), day, minutes);

        cbHour.removeAllItems();
        for (LocalTime h : hours) {
            cbHour.addItem(h);
        }

        boolean has = cbHour.getItemCount() > 0;
        cbHour.setEnabled(has);
        btnCreate.setEnabled(has);
    }

    private void onCreate(ActionEvent e) {
        Location li = (Location) cbLocation.getSelectedItem();
        Integer minutes = getSelectedMinutes();
        LocalTime hour = (LocalTime) cbHour.getSelectedItem();

        if (li == null || minutes == null || hour == null) {
            JOptionPane.showMessageDialog(this, "Faltan datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        LocalDate day = getSelectedDay();
        if (day == null) {
            JOptionPane.showMessageDialog(this, "La fecha seleccionada no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Reservation r = controller.createReservation(this.userId, li.getId(), day, hour, minutes);
            JOptionPane.showMessageDialog(this, "Reserva creada (ID " + r.getId() + ").", "OK", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo crear", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

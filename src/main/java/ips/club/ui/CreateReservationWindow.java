package ips.club.ui;

import ips.club.controller.ReservationController;
import ips.club.model.Location;
import ips.club.model.Reservation;
import ips.club.model.WeatherForecast;
import ips.club.model.WeatherPolicy;

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

    private JComboBox<Location> cbLocation;
    private JXDatePicker dpDay;
    private JComboBox<LocalTime> cbHour;
    private JButton btnCreate;
    private JButton btnCancel;
    private JLabel lblHint;
    private JLabel lblForecast;
    private JToggleButton btn60;
    private JToggleButton btn120;
    private ButtonGroup durationGroup;

    private final ZoneId zone = ZoneId.systemDefault();
    private LocalDate minDate;
    private LocalDate maxDate;
    private LocalDate lastValidDay;
    private boolean weatherSuitable = true;

    public CreateReservationWindow(ReservationController controller, int userId) {
        super(null, "Nueva reserva", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.userId = userId;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(560, 320);
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
        lblForecast = new JLabel("Previsión: --");
        btn60 = new JToggleButton("60 minutos");
        btn120 = new JToggleButton("120 minutos");
        durationGroup = new ButtonGroup();
        durationGroup.add(btn60);
        durationGroup.add(btn120);
        btn60.setSelected(true);

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
        form.add(new JLabel("Previsión:"), c);
        c.gridx = 1;
        form.add(lblForecast, c);

        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        durationPanel.add(btn60);
        durationPanel.add(btn120);

        c.gridy++;
        c.gridx = 0;
        form.add(new JLabel("Duración:"), c);
        c.gridx = 1;
        form.add(durationPanel, c);

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
        cbHour.setEnabled(false);
        btnCreate.setEnabled(false);

        updateForecast();
        updateAvailableHours();

        cbLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateForecast();
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
                updateForecast();
                updateAvailableHours();
            }
        });

        btn120.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateForecast();
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

    private void loadLocations() {
        cbLocation.removeAllItems();
        List<Location> locations = controller.findAllLocations();
        for (Location l : locations) {
            cbLocation.addItem(l);
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

    private void restoreLastValidDay() {
        if (lastValidDay == null) {
            lastValidDay = minDate;
        }
        Date d = Date.from(lastValidDay.atStartOfDay(zone).toInstant());
        dpDay.setDate(d);
    }

    private LocalDate getSelectedDay() {
        Date selected = dpDay.getDate();
        if (selected == null) {
            return null;
        }
        return Instant.ofEpochMilli(selected.getTime()).atZone(zone).toLocalDate();
    }

    private Integer getSelectedMinutes() {
        if (btn60.isSelected()) {
            return 60;
        }
        if (btn120.isSelected()) {
            return 120;
        }
        return null;
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
        updateForecast();
        updateAvailableHours();
    }

    private void updateForecast() {
        LocalDate day = getSelectedDay();
        Location location = (Location) cbLocation.getSelectedItem();
        if (day == null) {
            lblForecast.setText("Previsión no disponible");
            lblForecast.setForeground(UIManager.getColor("Label.foreground"));
            weatherSuitable = true;
            return;
        }
        try {
            WeatherForecast forecast = controller.getDailyForecast(day);
            if (forecast == null) {
                lblForecast.setText("Previsión no disponible");
                lblForecast.setForeground(UIManager.getColor("Label.foreground"));
                weatherSuitable = true;
                return;
            }
            if (location != null && !location.isOutdoor()) {
                weatherSuitable = true;
                String text = String.format(
                        "Min %.1f ºC, max %.1f ºC, precipitación %.1f mm (instalación interior)",
                        forecast.getMinTemperatureCelsius(),
                        forecast.getMaxTemperatureCelsius(),
                        forecast.getPrecipitationMm());
                lblForecast.setForeground(UIManager.getColor("Label.foreground"));
                lblForecast.setText(text);
            } else {
                weatherSuitable = WeatherPolicy.isSuitableForLocation(location, forecast);
                String text = String.format(
                        "Min %.1f ºC, max %.1f ºC, precipitación %.1f mm",
                        forecast.getMinTemperatureCelsius(),
                        forecast.getMaxTemperatureCelsius(),
                        forecast.getPrecipitationMm());
                if (weatherSuitable) {
                    lblForecast.setForeground(UIManager.getColor("Label.foreground"));
                    lblForecast.setText(text + " (condiciones adecuadas)");
                } else {
                    lblForecast.setForeground(Color.RED);
                    lblForecast.setText(text + " (condiciones no adecuadas)");
                }
            }
        } catch (Exception ex) {
            lblForecast.setText("Previsión no disponible");
            lblForecast.setForeground(UIManager.getColor("Label.foreground"));
            weatherSuitable = true;
        }
    }

    private void updateAvailableHours() {
        Location li = (Location) cbLocation.getSelectedItem();
        Integer minutes = getSelectedMinutes();
        LocalDate day = getSelectedDay();

        if (li == null || minutes == null || day == null) {
            cbHour.removeAllItems();
            cbHour.setEnabled(false);
            btnCreate.setEnabled(false);
            lblHint.setText("Elige instalación, día y duración para ver horas libres");
            return;
        }

        List<LocalTime> hours = controller.computeAvailableStartHours(this.userId, li.getId(), day, minutes);

        cbHour.removeAllItems();
        for (LocalTime h : hours) {
            cbHour.addItem(h);
        }

        boolean has = cbHour.getItemCount() > 0;
        cbHour.setEnabled(has);
        btnCreate.setEnabled(has && weatherSuitable);
        if (!has) {
            lblHint.setText("No hay horas disponibles con esos datos.");
        } else if (!weatherSuitable && li.isOutdoor()) {
            lblHint.setText("Las condiciones meteorológicas no son óptimas.");
        } else {
            lblHint.setText("Selecciona la hora y pulsa Reservar.");
        }
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

        if (!weatherSuitable && li.isOutdoor()) {
            JOptionPane.showMessageDialog(this, "No se pueden realizar reservas por las condiciones meteorológicas.", "No disponible", JOptionPane.WARNING_MESSAGE);
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

package ips.club.ui;

import ips.club.controller.AssemblyController;
import ips.club.model.Assembly;
import ips.club.model.AssemblyType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AssemblyCreateWindow extends JDialog {

    private final AssemblyController controller;

    private final JTextField tfTitle;
    private final JTextArea taDesc;
    private final JTextField tfDay;
    private final JTextField tfHour;
    private final JComboBox<AssemblyType> cbType;

    private final JButton btnOk;
    private final JButton btnCancel;

    private Assembly created;

    public AssemblyCreateWindow(Window owner, AssemblyController controller) {
        super(owner, "Crear asamblea", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        tfTitle = new JTextField(28);
        taDesc = new JTextArea(5, 28);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        tfDay = new JTextField(10);
        tfHour = new JTextField(6);
        cbType = new JComboBox<AssemblyType>(new AssemblyType[]{AssemblyType.ORDINARY, AssemblyType.EXTRAORDINARY});

        tfDay.setToolTipText("yyyy-MM-dd");
        tfHour.setToolTipText("HH:mm");

        btnOk = new JButton("Crear");
        btnCancel = new JButton("Cancelar");

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int r = 0;

        gc.gridx = 0; gc.gridy = r;
        form.add(new JLabel("Título:"), gc);
        gc.gridx = 1; gc.gridy = r++;
        form.add(tfTitle, gc);

        gc.gridx = 0; gc.gridy = r;
        form.add(new JLabel("Descripción:"), gc);
        gc.gridx = 1; gc.gridy = r++;
        form.add(new JScrollPane(taDesc), gc);

        gc.gridx = 0; gc.gridy = r;
        form.add(new JLabel("Tipo:"), gc);
        gc.gridx = 1; gc.gridy = r++;
        form.add(cbType, gc);

        gc.gridx = 0; gc.gridy = r;
        form.add(new JLabel("Día (yyyy-MM-dd):"), gc);
        gc.gridx = 1; gc.gridy = r++;
        form.add(tfDay, gc);

        gc.gridx = 0; gc.gridy = r;
        form.add(new JLabel("Hora (HH:mm):"), gc);
        gc.gridx = 1; gc.gridy = r++;
        form.add(tfHour, gc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.add(btnOk);
        actions.add(btnCancel);

        setLayout(new BorderLayout(8, 8));
        add(form, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        btnOk.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { onCreate(); }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { created = null; dispose(); }
        });

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(560, 380);
        setLocationRelativeTo(owner);

        LocalDateTime now = LocalDateTime.now();
        tfDay.setText(now.toLocalDate().toString());
        tfHour.setText(now.toLocalTime().withSecond(0).withNano(0).toString().substring(0,5));
        cbType.setSelectedItem(AssemblyType.ORDINARY);
    }

    private void onCreate() {
        String title = tfTitle.getText() != null ? tfTitle.getText().trim() : "";
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String dayStr = tfDay.getText() != null ? tfDay.getText().trim() : "";
        String hourStr = tfHour.getText() != null ? tfHour.getText().trim() : "";
        if (dayStr.isEmpty() || hourStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Día y hora son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate date;
        LocalTime time;
        try {
            date = LocalDate.parse(dayStr);
            time = LocalTime.parse(hourStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato de fecha/hora inválido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDateTime scheduled = date.atTime(time);
        if (scheduled.isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this, "No se puede programar una asamblea en el pasado.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AssemblyType type = (AssemblyType) cbType.getSelectedItem();
        String desc = taDesc.getText();
        try {
            created = controller.createScheduled(title, desc, scheduled, type);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Assembly getCreated() {
        return created;
    }
}

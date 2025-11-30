package ips.club.ui;

import ips.club.model.Assembly;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("serial")
public class AssemblyDetailWindow extends JDialog {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public AssemblyDetailWindow(Window owner, Assembly a) {
        super(owner, "Detalle de asamblea", ModalityType.APPLICATION_MODAL);

        JPanel meta = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        int r = 0;
        addRow(meta, gc, r++, "ID:", a.getId() != null ? String.valueOf(a.getId()) : "");
        addRow(meta, gc, r++, "Título:", safe(a.getTitle()));
        addRow(meta, gc, r++, "Estado:", a.getStatus() != null ? a.getStatus().name() : "");
        addRow(meta, gc, r++, "Programada:", fmt(a.getScheduledAt()));
        addRow(meta, gc, r++, "Creada:", fmt(a.getCreatedAt()));

        JTextArea taDesc = new JTextArea(8, 60);
        taDesc.setText(safe(a.getDescription()));
        taDesc.setEditable(false);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);

        JPanel descPanel = new JPanel(new BorderLayout(4, 4));
        descPanel.add(new JLabel("Descripción:"), BorderLayout.NORTH);
        descPanel.add(new JScrollPane(
                taDesc,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

        JPanel minutesPanel = new JPanel(new BorderLayout(4, 4));
        minutesPanel.add(new JLabel("Acta:"), BorderLayout.NORTH);

        String minutes = safe(a.getMinutesText());
        if (minutes.isEmpty()) {
            JTextArea taMinutes = new JTextArea(6, 60);
            taMinutes.setText("No hay acta registrada.");
            taMinutes.setEditable(false);
            taMinutes.setLineWrap(true);
            taMinutes.setWrapStyleWord(true);
            minutesPanel.add(new JScrollPane(
                    taMinutes,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        } else {
            File file = new File(minutes);
            if (file.exists() && file.isFile()) {
                JLabel lblName = new JLabel("Acta registrada: " + file.getName());
                JButton btnOpen = new JButton("Abrir acta");
                btnOpen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!Desktop.isDesktopSupported()) {
                            JOptionPane.showMessageDialog(
                                    AssemblyDetailWindow.this,
                                    "No se puede abrir el archivo en este sistema.",
                                    "Aviso",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        try {
                            Desktop.getDesktop().open(file);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(
                                    AssemblyDetailWindow.this,
                                    "No se pudo abrir el archivo:\n" + ex.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
                filePanel.add(lblName);
                filePanel.add(btnOpen);
                minutesPanel.add(filePanel, BorderLayout.CENTER);
            } else {
                JTextArea taMinutes = new JTextArea(14, 60);
                taMinutes.setText(minutes);
                taMinutes.setEditable(false);
                taMinutes.setLineWrap(true);
                taMinutes.setWrapStyleWord(true);
                minutesPanel.add(new JScrollPane(
                        taMinutes,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
            }
        }

        final JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, descPanel, minutesPanel);
        split.setResizeWeight(0.35);
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);

        JButton btnClose = new JButton("Cerrar");
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnClose);

        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel northWrap = new JPanel(new BorderLayout());
        northWrap.add(meta, BorderLayout.CENTER);

        setLayout(new BorderLayout(8, 8));
        add(northWrap, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(actions, BorderLayout.SOUTH);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(800, 650);
        setLocationRelativeTo(owner);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                split.setDividerLocation(0.35);
            }
        });
    }

    private static void addRow(JPanel panel, GridBagConstraints gc, int row, String label, String value) {
        GridBagConstraints l = (GridBagConstraints) gc.clone();
        l.gridx = 0;
        l.gridy = row;
        l.weightx = 0;
        l.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), l);

        GridBagConstraints v = (GridBagConstraints) gc.clone();
        v.gridx = 1;
        v.gridy = row;
        v.weightx = 1;
        v.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JTextField(value) {
            {
                setEditable(false);
                setBorder(BorderFactory.createEmptyBorder());
                setOpaque(false);
            }
        }, v);
    }

    private static String fmt(LocalDateTime dt) {
        return dt == null ? "" : dt.withNano(0).format(DF);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}

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

@SuppressWarnings("serial")
public class AssemblyCreateWindow extends JDialog {

    private final AssemblyController controller;

    private final JTextField tfTitle;
    private final JComboBox<AssemblyType> cbType;
    private final JTextField tfDate;
    private final JTextField tfTime;
    private final JTextArea taDescription;

    private final DefaultListModel<String> agendaModel;
    private final JList<String> lstAgenda;
    private final JTextField tfAgendaItem;
    private final JButton btnAddPoint;
    private final JButton btnRemovePoint;

    private final JButton btnAccept;
    private final JButton btnCancel;

    private Assembly created;

    public AssemblyCreateWindow(Window owner, AssemblyController controller) {
        super(owner, "Nueva asamblea", ModalityType.APPLICATION_MODAL);
        this.controller = controller;

        tfTitle = new JTextField(30);
        cbType = new JComboBox<AssemblyType>(AssemblyType.values());
        tfDate = new JTextField(10);
        tfTime = new JTextField(5);
        taDescription = new JTextArea(4, 40);

        agendaModel = new DefaultListModel<String>();
        lstAgenda = new JList<String>(agendaModel);
        tfAgendaItem = new JTextField(30);
        btnAddPoint = new JButton("Añadir punto");
        btnRemovePoint = new JButton("Eliminar punto");

        btnAccept = new JButton("Crear asamblea");
        btnCancel = new JButton("Cancelar");

        taDescription.setLineWrap(true);
        taDescription.setWrapStyleWord(true);

        lstAgenda.setVisibleRowCount(6);
        lstAgenda.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        LocalDate today = LocalDate.now();
        tfDate.setText(today.toString());
        tfTime.setText("18:00");

        setLayout(new BorderLayout(8, 8));
        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonsPanel(), BorderLayout.SOUTH);

        btnAddPoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddPoint();
            }
        });
        btnRemovePoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemovePoint();
            }
        });
        btnAccept.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAccept();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(owner);
    }

    private JPanel buildFormPanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 4, 6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;

        int r = 0;

        gc.gridx = 0;
        gc.gridy = r;
        pnl.add(new JLabel("Título:"), gc);
        gc.gridx = 1;
        gc.gridy = r++;
        pnl.add(tfTitle, gc);

        gc.gridx = 0;
        gc.gridy = r;
        pnl.add(new JLabel("Tipo:"), gc);
        gc.gridx = 1;
        gc.gridy = r++;
        pnl.add(cbType, gc);

        gc.gridx = 0;
        gc.gridy = r;
        pnl.add(new JLabel("Fecha (AAAA-MM-DD):"), gc);
        gc.gridx = 1;
        gc.gridy = r++;
        pnl.add(tfDate, gc);

        gc.gridx = 0;
        gc.gridy = r;
        pnl.add(new JLabel("Hora (HH:MM):"), gc);
        gc.gridx = 1;
        gc.gridy = r++;
        pnl.add(tfTime, gc);

        gc.gridx = 0;
        gc.gridy = r;
        gc.gridwidth = 2;
        pnl.add(new JLabel("Descripción:"), gc);
        gc.gridy = r + 1;
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.add(new JScrollPane(taDescription,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
        pnl.add(descPanel, gc);
        r += 2;

        gc.gridx = 0;
        gc.gridy = r;
        gc.gridwidth = 2;
        pnl.add(new JLabel("Puntos del orden del día:"), gc);

        JPanel agendaEditor = new JPanel(new GridBagLayout());
        GridBagConstraints gc2 = new GridBagConstraints();
        gc2.insets = new Insets(2, 2, 2, 2);
        gc2.anchor = GridBagConstraints.WEST;
        gc2.fill = GridBagConstraints.HORIZONTAL;
        gc2.weightx = 1.0;

        gc2.gridx = 0;
        gc2.gridy = 0;
        agendaEditor.add(tfAgendaItem, gc2);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        btns.add(btnAddPoint);
        btns.add(btnRemovePoint);
        gc2.gridx = 1;
        gc2.gridy = 0;
        gc2.weightx = 0.0;
        agendaEditor.add(btns, gc2);

        gc2.gridx = 0;
        gc2.gridy = 1;
        gc2.gridwidth = 2;
        gc2.weightx = 1.0;
        gc2.weighty = 1.0;
        gc2.fill = GridBagConstraints.BOTH;
        JScrollPane spList = new JScrollPane(lstAgenda,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        agendaEditor.add(spList, gc2);

        gc.gridx = 0;
        gc.gridy = r + 1;
        gc.gridwidth = 2;
        gc.weightx = 1.0;
        gc.weighty = 1.0;
        gc.fill = GridBagConstraints.BOTH;
        pnl.add(agendaEditor, gc);

        return pnl;
    }

    private JPanel buildButtonsPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnl.add(btnAccept);
        pnl.add(btnCancel);
        return pnl;
    }

    private void onAddPoint() {
        String text = tfAgendaItem.getText();
        if (text == null) {
            text = "";
        }
        text = text.trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El texto del punto no puede estar vacío.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        agendaModel.addElement(text);
        tfAgendaItem.setText("");
        tfAgendaItem.requestFocusInWindow();
    }

    private void onRemovePoint() {
        int idx = lstAgenda.getSelectedIndex();
        if (idx >= 0) {
            agendaModel.remove(idx);
        }
    }

    private void onAccept() {
        String title = tfTitle.getText();
        if (title == null) {
            title = "";
        }
        title = title.trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dateText = tfDate.getText() != null ? tfDate.getText().trim() : "";
        String timeText = tfTime.getText() != null ? tfTime.getText().trim() : "";

        LocalDate date;
        LocalTime time;
        try {
            date = LocalDate.parse(dateText);
            time = LocalTime.parse(timeText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fecha u hora no válidas.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDateTime scheduled = LocalDateTime.of(date, time);
        String baseDesc = taDescription.getText();

        if (baseDesc == null) {
            baseDesc = "";
        }
        baseDesc = baseDesc.trim();

        StringBuilder descBuilder = new StringBuilder();
        if (!baseDesc.isEmpty()) {
            descBuilder.append(baseDesc).append("\n\n");
        }
        if (!agendaModel.isEmpty()) {
            descBuilder.append("ORDEN DEL DÍA:\n");
            for (int i = 0; i < agendaModel.size(); i++) {
                String item = agendaModel.getElementAt(i);
                descBuilder.append(i + 1).append(". ").append(item).append("\n");
            }
        }
        String desc = descBuilder.toString();

        AssemblyType type = (AssemblyType) cbType.getSelectedItem();
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

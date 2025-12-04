package ips.club.ui;

import ips.club.controller.AssemblyController;
import ips.club.model.Assembly;
import ips.club.model.AssemblyType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("serial")
public class AssemblyMinutesEditorWindow extends JDialog {

    private static final DateTimeFormatter DF_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DF_TIME = DateTimeFormatter.ofPattern("HH:mm");

    private final AssemblyController controller;
    private final int assemblyId;

    private final DefaultListModel<AgendaItem> agendaModel = new DefaultListModel<AgendaItem>();
    private final JList<AgendaItem> lstAgenda = new JList<AgendaItem>(agendaModel);
    private final JTextField tfItemTitle = new JTextField(28);
    private final JTextArea taItemNotes = new JTextArea(3, 28);
    private final JButton btnAddItem = new JButton("Añadir punto");
    private final JButton btnRemoveItem = new JButton("Eliminar seleccionado");

    private final JTextArea taRuegos = new JTextArea(6, 40);

    private final JButton btnSave = new JButton("Guardar acta");
    private final JButton btnCancel = new JButton("Cancelar");

    private Assembly updated;

    public AssemblyMinutesEditorWindow(Window owner, AssemblyController controller, int assemblyId) {
        super(owner, "Crear acta", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.assemblyId = assemblyId;

        lstAgenda.setVisibleRowCount(8);
        lstAgenda.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        taItemNotes.setLineWrap(true);
        taItemNotes.setWrapStyleWord(true);
        taRuegos.setLineWrap(true);
        taRuegos.setWrapStyleWord(true);

        JPanel pnlEditor = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 4, 4, 4);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        int r = 0;

        gc.gridx = 0;
        gc.gridy = r;
        pnlEditor.add(new JLabel("Título del punto:"), gc);
        gc.gridx = 1;
        gc.gridy = r++;
        pnlEditor.add(tfItemTitle, gc);

        gc.gridx = 0;
        gc.gridy = r;
        pnlEditor.add(new JLabel("Notas (opcional):"), gc);
        gc.gridx = 1;
        gc.gridy = r++;
        pnlEditor.add(new JScrollPane(taItemNotes), gc);

        JPanel pnlEditorBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlEditorBtns.add(btnAddItem);
        pnlEditorBtns.add(btnRemoveItem);
        gc.gridx = 1;
        gc.gridy = r++;
        pnlEditor.add(pnlEditorBtns, gc);

        JPanel pnlAgenda = new JPanel(new BorderLayout(4, 4));
        pnlAgenda.add(new JLabel("Orden del día / Puntos a tratar:"), BorderLayout.NORTH);
        pnlAgenda.add(new JScrollPane(lstAgenda), BorderLayout.CENTER);

        JPanel pnlRuegos = new JPanel(new BorderLayout(4, 4));
        pnlRuegos.add(new JLabel("Ruegos y preguntas (opcional):"), BorderLayout.NORTH);
        pnlRuegos.add(new JScrollPane(taRuegos), BorderLayout.CENTER);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlActions.add(btnSave);
        pnlActions.add(btnCancel);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(6, 6, 6, 6);
        c2.gridx = 0;
        c2.gridy = 0;
        c2.weightx = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        center.add(pnlEditor, c2);
        c2.gridy = 1;
        c2.weighty = 1;
        c2.fill = GridBagConstraints.BOTH;
        center.add(pnlAgenda, c2);
        c2.gridy = 2;
        center.add(pnlRuegos, c2);

        setLayout(new BorderLayout(6, 6));
        add(center, BorderLayout.CENTER);
        add(pnlActions, BorderLayout.SOUTH);

        btnAddItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddItem();
            }
        });
        btnRemoveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRemoveItem();
            }
        });
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSave();
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

    private void onAddItem() {
        String title = tfItemTitle.getText() != null ? tfItemTitle.getText().trim() : "";
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El título del punto es obligatorio.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String notes = taItemNotes.getText() != null ? taItemNotes.getText().trim() : "";
        agendaModel.addElement(new AgendaItem(title, notes));
        tfItemTitle.setText("");
        taItemNotes.setText("");
        tfItemTitle.requestFocusInWindow();
    }

    private void onRemoveItem() {
        int i = lstAgenda.getSelectedIndex();
        if (i >= 0) agendaModel.remove(i);
    }

    private void onSave() {
        if (agendaModel.getSize() < 1) {
            JOptionPane.showMessageDialog(this, "Debes añadir al menos un ítem del orden del día.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String minutes = buildMinutesText();
        Assembly updated = controller.attachMinutesAndMarkWaiting(assemblyId, minutes);
        if (updated == null) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el acta.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.updated = updated;
        dispose();
    }

    private String buildMinutesText() {
        Assembly a = controller.get(assemblyId);

        String title = a != null && a.getTitle() != null ? a.getTitle().trim() : "";
        if (title.isEmpty()) title = "Asamblea del club";

        String tipo = "";
        if (a != null && a.getType() != null) {
            AssemblyType t = a.getType();
            if (t == AssemblyType.ORDINARY) tipo = "ORDINARIA";
            else if (t == AssemblyType.EXTRAORDINARY) tipo = "EXTRAORDINARIA";
        }

        LocalDateTime when = a != null ? a.getScheduledAt() : null;
        String fecha = "";
        String hora = "";
        if (when != null) {
            fecha = when.toLocalDate().format(DF_DATE);
            hora = when.toLocalTime().withSecond(0).withNano(0).format(DF_TIME);
        }

        StringBuilder sb = new StringBuilder();

        sb.append("ACTA DE LA ASAMBLEA GENERAL");
        if (!tipo.isEmpty()) sb.append(" ").append(tipo);
        sb.append("\n");
        sb.append(title).append("\n");
        if (!fecha.isEmpty()) {
            sb.append(fecha);
            if (!hora.isEmpty()) sb.append(" - ").append(hora).append(" horas");
            sb.append("\n");
        }
        sb.append("\n");
        sb.append("En la fecha y hora indicadas se reúne la asamblea del club para tratar los asuntos incluidos en el siguiente orden del día.\n\n");

        sb.append("ORDEN DEL DÍA\n");
        for (int i = 0; i < agendaModel.getSize(); i++) {
            AgendaItem it = agendaModel.getElementAt(i);
            sb.append(i + 1).append(". ").append(it.title).append("\n");
        }
        sb.append("\n");

        sb.append("DESARROLLO DE LA SESIÓN\n");
        for (int i = 0; i < agendaModel.getSize(); i++) {
            AgendaItem it = agendaModel.getElementAt(i);
            sb.append(i + 1).append(". ").append(it.title).append("\n");
            String notes = it.notes != null ? it.notes.trim() : "";
            if (notes.isEmpty()) {
                sb.append("   Sin observaciones específicas.\n");
            } else {
                String[] lines = notes.split("\\R");
                for (String line : lines) {
                    String l = line != null ? line.trim() : "";
                    if (!l.isEmpty()) sb.append("   - ").append(l).append("\n");
                }
            }
            sb.append("\n");
        }

        String rq = taRuegos.getText() != null ? taRuegos.getText().trim() : "";
        sb.append("RUEGOS Y PREGUNTAS\n");
        if (rq.isEmpty()) {
            sb.append("Sin ruegos ni preguntas.\n");
        } else {
            String[] lines = rq.split("\\R");
            for (String line : lines) {
                String l = line != null ? line.trim() : "";
                if (!l.isEmpty()) sb.append("- ").append(l).append("\n");
            }
        }

        sb.append("\nY no habiendo más asuntos que tratar, se levanta la sesión, de lo que yo, como secretario, doy fe.\n\n");
        sb.append("VºBº Presidencia\n");
        sb.append("Firma del secretario\n");

        return sb.toString();
    }

    public Assembly getUpdated() {
        return updated;
    }

    private static final class AgendaItem {
        final String title;
        final String notes;
        AgendaItem(String title, String notes) {
            this.title = title;
            this.notes = notes;
        }
        @Override
        public String toString() {
            return title;
        }
    }
}
